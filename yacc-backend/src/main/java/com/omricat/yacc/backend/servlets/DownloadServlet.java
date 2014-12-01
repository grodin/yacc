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
import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;
import com.omricat.yacc.backend.api.CurrencyService;
import com.omricat.yacc.data.Currencies;
import com.omricat.yacc.data.Currency;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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

    private static final Logger log = Logger.getLogger(DownloadServlet.class
            .getName());

    private final CurrencyService service;
    private final RestAdapter restAdapter;

    private final GcsService gcsService = GcsServiceFactory
            .createGcsService(new RetryParams.Builder()
                    .initialRetryDelayMillis(10)
                    .retryMaxAttempts(10)
                    .totalRetryPeriodMillis(15000)
                    .build());

    private final ObjectMapper mapper;

    private final Config config = new Config();

    public DownloadServlet() {
        super();
        mapper = new ObjectMapper();
        mapper.enable(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS);
        restAdapter = new RestAdapter.Builder()
                .setEndpoint(config.endpoint)
                .setClient(new UrlFetchClient())
                .setConverter(new JacksonConverter(mapper))
                .build();
        service = restAdapter.create(CurrencyService.class);
    }

    @Override
    protected void doGet(final HttpServletRequest req,
                         final HttpServletResponse resp) throws
            ServletException, IOException {
        if (true || req.getHeader("X-AppEngine-Cron") != null) {
            final Map<String, String> map = service.getLatestCurrencies().get
                    (0);
            final long timeStamp = Long.valueOf(map.remove("DateTime"));
            final List<Currency> currencyList = new LinkedList<>();

            for (Map.Entry<String, String> entry : map.entrySet()) {
                /* The currency service gives currency values which end
                    with "\r" so we remove it from each
                 */
                String currencyValue = entry.getValue().replace("\r", "");
                currencyList.add(new Currency(currencyValue, entry.getKey()));
            }

            Currencies currencies = new Currencies(currencyList
                    .toArray(new Currency[currencyList.size()]), timeStamp);

            final GcsFilename filename = new GcsFilename(config.bucket,
                    config.filename);
            try {
                writeToStore(currencies, filename);
                resp.setContentType("application/json");
                mapper.writeValue(resp.getWriter(), currencies);
            } catch (IOException e) {
                log(Level.WARNING, e);
                throw e;
            }
        }
    }

    private static void log(final Level level, final Throwable e) {
        log.log(level, "Exception: ", e);
    }

    // Note close is not called  in a finally block because files aren't
    // written until they are closed. So if an exception is thrown,
    // the file will remain as it was before this method was called

    private void writeToStore(final Currencies currencies, final GcsFilename
            filename) throws IOException {

        final GcsFileOptions fileOptions = new GcsFileOptions.Builder()
                .mimeType("application/json")
                .build();

        final GcsOutputChannel outputChannel = gcsService.createOrReplace
                (filename, fileOptions);
        final OutputStream stream = Channels.newOutputStream
                (outputChannel);
        mapper.writeValue(stream, currencies);
        outputChannel.close();

    }
}

