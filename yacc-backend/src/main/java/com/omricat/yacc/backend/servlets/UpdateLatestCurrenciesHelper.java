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
import com.omricat.yacc.backend.Config;
import com.omricat.yacc.backend.api.CurrencyService;
import com.omricat.yacc.backend.api.NamesService;
import com.omricat.yacc.backend.datastore.CurrenciesStore;
import com.omricat.yacc.backend.datastore.NamesStore;
import com.omricat.yacc.model.CurrencyDataset;

import java.io.IOException;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import retrofit.RestAdapter;
import retrofit.appengine.UrlFetchClient;
import retrofit.converter.JacksonConverter;

class UpdateLatestCurrenciesHelper {
    final Logger log = Logger.getLogger
            (UpdateLatestCurrenciesHelper.class
                    .getName());
    CurrenciesProcessor currenciesProcessor;
    final GcsFilename gcsFilename;
    final ObjectMapper mapper = new ObjectMapper();

    private UpdateLatestCurrenciesHelper() {
        this.gcsFilename = new GcsFilename(Config
                .BUCKET,
                Config.LATEST_CURRENCY_FILENAME);
    }

    public static UpdateLatestCurrenciesHelper newInstance() {
        return new UpdateLatestCurrenciesHelper();
    }

    void init() throws ServletException {
        mapper.enable(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS);

        final RestAdapter.Builder builder = new RestAdapter.Builder();
        RestAdapter restAdapter = builder
                .setEndpoint(Config.CURRENCY_DATA_ENDPOINT)
                .setClient(new UrlFetchClient())
                .setConverter(new JacksonConverter(mapper))
                .build();
        final CurrencyService currencyService = restAdapter.create
                (CurrencyService.class);

        restAdapter = builder.setEndpoint(Config.CURRENCY_NAMES_ENDPOINT)
                .build();
        final NamesService namesService = restAdapter.create(NamesService
                .class);

        currenciesProcessor = new CurrenciesProcessor(
                currencyService, namesService, mapper,
                CurrenciesStore.getInstance(),
                NamesStore.getInstance());
    }

    void downloadCurrencies(final Writer out) throws IOException {
        CurrencyDataset currencyDataset = currenciesProcessor.download();
        try {
            currenciesProcessor.writeToStore(currencyDataset);
            mapper.writeValue(out, currencyDataset);
        } catch (IOException e) {
            log.log(Level.WARNING, "Caught exception", e);
            throw e;
        }
    }
}
