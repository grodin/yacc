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

import com.omricat.yacc.backend.datastore.CurrenciesStore;
import com.omricat.yacc.backend.util.IOUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LatestServlet extends HttpServlet {

    private static final int BUFFER_SIZE = 2 * 1024 * 1024;

    @Override
    protected void doGet(final HttpServletRequest req,
                         final HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");

        final InputStream inputStream = CurrenciesStore.getInstance()
                .getInputStream();

        final ServletOutputStream outputStream = resp.getOutputStream();

        try {
            IOUtils.copy(inputStream, outputStream,
                    BUFFER_SIZE);
        } catch (FileNotFoundException e) {

            final UpdateLatestCurrenciesHelper helper =
                    UpdateLatestCurrenciesHelper.newInstance();

            helper.init();
            helper.downloadCurrencies(outputStream);
        }
    }

}
