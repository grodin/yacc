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
import com.omricat.yacc.backend.util.HttpUtils;
import com.omricat.yacc.data.model.CurrencyCode;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import retrofit.RestAdapter;
import retrofit.appengine.UrlFetchClient;
import retrofit.converter.JacksonConverter;

public class CurrencyNamesServlet extends HttpServlet {

    private NamesHelper namesHelper;
    private ObjectMapper mapper = new ObjectMapper();

    @Override public void init() throws ServletException {

        final RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Config.CURRENCY_NAMES_ENDPOINT)
                .setClient(new UrlFetchClient())
                .setConverter(new JacksonConverter(this.mapper))
                .build();

        namesHelper = NamesHelper.getInstance(mapper,
                restAdapter.create(NamesService.class));
    }

    @Override
    protected void doGet(final HttpServletRequest req,
                         final HttpServletResponse resp)
            throws ServletException, IOException {
        Map<CurrencyCode, String> names = namesHelper
                .getAndStoreCurrencyNames(NamesStore.getInstance());

        HttpUtils.setJsonUTF8ContentType(resp);

        mapper.writeValue(resp.getWriter(), names);

    }

}
