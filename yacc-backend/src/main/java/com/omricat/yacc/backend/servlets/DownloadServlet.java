/*
 * Copyright 2014 Omricat Software
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.omricat.yacc.backend.servlets;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;
import com.omricat.yacc.backend.api.CurrencyService;
import com.omricat.yacc.data.Currencies;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import retrofit.RestAdapter;
import retrofit.appengine.UrlFetchClient;
import retrofit.converter.JacksonConverter;

public class DownloadServlet extends HttpServlet {

    private final Logger log = Logger.getLogger(DownloadServlet.class
            .getName());

    private final CurrenciesProcessor currenciesProcessor;
    private final Config config = new Config();
    private final GcsFilename gcsFilename = new GcsFilename(config.bucket,
            config.filename);
    private final GcsService gcsService = GcsServiceFactory
            .createGcsService(new RetryParams.Builder()
                    .initialRetryDelayMillis(10)
                    .retryMaxAttempts(10)
                    .totalRetryPeriodMillis(15000)
                    .build());
    private final ObjectMapper mapper = new ObjectMapper();

    public DownloadServlet() {
        super();
        mapper.enable(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS);

        final RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(config.endpoint)
                .setClient(new UrlFetchClient())
                .setConverter(new JacksonConverter(mapper))
                .build();
        final CurrencyService service = restAdapter.create(CurrencyService
                .class);

        currenciesProcessor = new CurrenciesProcessor(gcsService, service,
                mapper, log);
    }

    @Override
    protected void doGet(final HttpServletRequest req,
                         final HttpServletResponse resp) throws
            ServletException, IOException {
        if (true || req.getHeader("X-AppEngine-Cron") != null) {
            Currencies currencies = currenciesProcessor.download();
            try {
                currenciesProcessor.writeToStore(currencies, gcsFilename,
                        gcsService);
                resp.setContentType("application/json");
                mapper.writeValue(resp.getWriter(), currencies);
            } catch (IOException e) {
                log.log(Level.WARNING, "Caught exception", e);
                throw e;
            }

        }
    }


}

