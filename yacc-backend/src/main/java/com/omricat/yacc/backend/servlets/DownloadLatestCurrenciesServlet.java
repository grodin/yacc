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

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DownloadLatestCurrenciesServlet extends HttpServlet {

    private transient final UpdateLatestCurrenciesHelper helper =
            UpdateLatestCurrenciesHelper.newInstance();

    @Override
    public void init() throws ServletException {
        helper.init();
    }

    @Override
    protected void doGet(final HttpServletRequest req,
                         final HttpServletResponse resp) throws
            ServletException, IOException {
        if (req.getHeader("X-AppEngine-Cron") != null) {
            // This header is only set if we've been called
            //  as a cron job by AppEngine
            resp.setContentType("application/json");
            helper.downloadCurrencies(resp.getOutputStream());
        } else {
            // 403 Unauthorized response
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }


}

