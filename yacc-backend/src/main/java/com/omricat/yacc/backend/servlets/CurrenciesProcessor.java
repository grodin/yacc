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
import com.omricat.yacc.backend.api.CurrencyService;
import com.omricat.yacc.backend.datastore.CurrenciesStore;
import com.omricat.yacc.data.Currency;
import com.omricat.yacc.data.CurrencySet;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

class CurrenciesProcessor {

    private final ObjectMapper mapper;

    private final CurrencyService service;

    CurrenciesProcessor(@NotNull final CurrencyService service,
                        @NotNull final ObjectMapper mapper) {
        this.service = checkNotNull(service);
        this.mapper = checkNotNull(mapper);

    }

    @NotNull CurrencySet download() throws IOException {
        final Map<String, String> map = service.getLatestCurrencies()
                .get(0); // Should only be one element in the array
        final long timeStamp = Long.parseLong(map.remove("DateTime"));
        final List<Currency> currencyList = new LinkedList<>();

        for (Map.Entry<String, String> entry : map.entrySet()) {
                /* The currency service gives currency values which end
                    with "\r" so we remove it from each
                 */
            String currencyValue = entry.getValue().replace("\r", "");
            currencyList.add(new Currency(currencyValue,
                    entry.getKey(), null));
        }

        return new CurrencySet(currencyList
                .toArray(new Currency[currencyList.size()]), timeStamp);

    }

    // Note close is not called  in a finally block because files aren't
    // written until they are closed. So if an exception is thrown,
    // the file will remain as it was before this method was called
    public void writeToStore(@NotNull final CurrencySet currencySet)
            throws IOException {

        final OutputStream stream = CurrenciesStore.getInstance()
                .getOutputStream();
        mapper.writeValue(stream, currencySet);
        stream.close();

    }
}
