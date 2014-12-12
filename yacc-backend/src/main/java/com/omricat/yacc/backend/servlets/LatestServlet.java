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

import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsInputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LatestServlet extends HttpServlet {

    private static final int BUFFER_SIZE = 2 * 1024 * 1024;

    private final GcsService gcsService = GcsServiceFactory.createGcsService
            (new RetryParams.Builder()
                    .initialRetryDelayMillis(10)
                    .retryMaxAttempts(10)
                    .totalRetryPeriodMillis(15000)
                    .build());

    private final GcsFilename fileName = new GcsFilename(Config.bucket,
            Config.filename);

    @Override
    public void init() throws ServletException {

    }

    @Override
    protected void doGet(final HttpServletRequest req,
                         final HttpServletResponse resp) throws
            ServletException, IOException {
        resp.setContentType("application/json");
        GcsInputChannel readChannel = gcsService.openPrefetchingReadChannel
                (fileName, 0, BUFFER_SIZE);
        copy(Channels.newInputStream(readChannel), resp.getOutputStream());
    }

    /**
     * Transfer the data from the inputStream to the outputStream. Then close
     * both streams.
     */
    private void copy(InputStream input, OutputStream output) throws
            IOException {
        try {
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead = input.read(buffer);
            while (bytesRead != -1) {
                output.write(buffer, 0, bytesRead);
                bytesRead = input.read(buffer);
            }
        } finally {
            input.close();
            output.close();
        }
    }
}