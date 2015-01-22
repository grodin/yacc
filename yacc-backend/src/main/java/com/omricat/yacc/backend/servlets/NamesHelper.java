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
import com.omricat.yacc.backend.Config;
import com.omricat.yacc.backend.api.NamesService;
import com.omricat.yacc.backend.datastore.NamesStore;
import com.omricat.yacc.model.CurrencyCode;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.Map;

import retrofit.RestAdapter;
import retrofit.appengine.UrlFetchClient;
import retrofit.converter.JacksonConverter;

import static com.google.common.base.Preconditions.checkNotNull;

class NamesHelper implements Serializable {
    final ObjectMapper mapper;
    final NamesService service;

    private NamesHelper(@NotNull final ObjectMapper mapper) {
        this.mapper = checkNotNull(mapper);
        final RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Config.CURRENCY_NAMES_ENDPOINT)
                .setClient(new UrlFetchClient())
                .setConverter(new JacksonConverter(this.mapper))
                .build();

        service = restAdapter.create(NamesService.class);

    }

    public static NamesHelper getInstance(@NotNull final ObjectMapper mapper) {
        return new NamesHelper(mapper);
    }

    Map<CurrencyCode, String> getAndStoreCurrencyNames()
            throws IOException {

        final Map<CurrencyCode, String> names = service.getCurrencyNames();
        try (Writer out = NamesStore.getInstance().getWriter()) {
            mapper.writeValue(out, names);
        }

        return names;
    }
}
