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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.omricat.yacc.backend.api.CurrencyService;
import com.omricat.yacc.data.Currencies;
import com.omricat.yacc.data.Currency;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkNotNull;

class CurrenciesProcessor {

    private final Logger log;

    private final GcsService gcsService;

    private final ObjectMapper mapper;

    private final CurrencyService service;

    CurrenciesProcessor(@NotNull final GcsService gcsService,
                        @NotNull final CurrencyService service,
                        @NotNull final ObjectMapper mapper,
                        @NotNull final Logger log) {
        this.gcsService = checkNotNull(gcsService);
        this.service = checkNotNull(service);
        this.mapper = checkNotNull(mapper);
        this.log = checkNotNull(log);

    }

    void log(final Level level, final Throwable e) {
        log.log(level, "Exception: ", e);
    }

    Currencies download() throws IOException {
        final Map<String, String> map = service.getLatestCurrencies()
                .get(0); // Should only be one element in the array
        final long timeStamp = Long.valueOf(map.remove("DateTime"));
        final List<Currency> currencyList = new LinkedList<>();

        for (Map.Entry<String, String> entry : map.entrySet()) {
                /* The currency service gives currency values which end
                    with "\r" so we remove it from each
                 */
            String currencyValue = entry.getValue().replace("\r", "");
            currencyList.add(new Currency(currencyValue,
                    entry.getKey()));
        }

        return new Currencies(currencyList
                .toArray(new Currency[currencyList.size()]), timeStamp);

    }

    // Note close is not called  in a finally block because files aren't
    // written until they are closed. So if an exception is thrown,
    // the file will remain as it was before this method was called
    public void writeToStore(@NotNull final Currencies currencies,
                             @NotNull final GcsFilename filename,
                             @NotNull final GcsService gcsService)
            throws IOException {

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
