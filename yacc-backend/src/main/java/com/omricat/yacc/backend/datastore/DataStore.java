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

package com.omricat.yacc.backend.datastore;

import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsInputChannel;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;
import com.google.common.base.Preconditions;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;

import static com.google.common.base.Preconditions.checkNotNull;

class DataStore {

    public static final GcsService DEFAULT_GCS_SERVICE = GcsServiceFactory
            .createGcsService(new RetryParams.Builder()
                    .initialRetryDelayMillis(10)
                    .retryMaxAttempts(10)
                    .totalRetryPeriodMillis(15000)
                    .build());

    public static final int DEFAULT_BUFFER_SIZE = 2 * 1024 * 1024;

    public static final GcsFileOptions DEFAULT_FILE_OPTIONS = new GcsFileOptions
            .Builder()
            .mimeType("application/json")
            .build();

    protected final GcsFilename gcsFilename;
    protected final GcsService gcsService;
    protected final int bufferSize;

    protected DataStore(@NotNull final GcsFilename gcsFilename,
                     @NotNull final GcsService gcsService,
                     final int bufferSize) {

        Preconditions.checkArgument(bufferSize > 0);
        this.bufferSize = bufferSize;
        this.gcsFilename = checkNotNull(gcsFilename);
        this.gcsService = checkNotNull(gcsService);
    }

    /**
     * Fully-specified factory method, to allow injecting of any needed
     * dependencies.
     *
     * @param gcsFilename GcsFilename to read/write from/to, not null
     * @param gcsService  GcsService client, not null
     * @param bufferSize  size of buffer to use in streaming, must be positive.
     */
    @NotNull public static DataStore getInstance(@NotNull final GcsFilename
                                                  gcsFilename,
                                       @NotNull final GcsService gcsService,
                                       final int bufferSize) {
        return new DataStore(gcsFilename, gcsService, bufferSize);
    }

    /**
     * Returns an {@link java.io.InputStream} representing the Currencies
     * store.
     */
    @NotNull public InputStream getInputStream() {
        final GcsInputChannel readChannel = gcsService
                .openPrefetchingReadChannel
                        (gcsFilename, 0, bufferSize);
        return Channels.newInputStream(readChannel);
    }

    /**
     * Returns an {@link OutputStream} representing the Currencies store.
     *
     * @param fileOptions instance of {@link GcsFileOptions} to use when opening
     *                    the stream, not null.
     * @throws IOException
     */
    @NotNull public OutputStream getOutputStream(@NotNull final GcsFileOptions
                                                  fileOptions)
            throws IOException {
        final GcsOutputChannel outputChannel = gcsService.createOrReplace
                (gcsFilename, checkNotNull(fileOptions));
        return Channels.newOutputStream
                (outputChannel);
    }

    /**
     * Convenience method which returns an {@link OutputStream} representing the
     * Currencies store, using {@link #DEFAULT_FILE_OPTIONS}.
     *
     * @throws IOException
     */
    @NotNull public OutputStream getOutputStream() throws IOException {
        return getOutputStream(DEFAULT_FILE_OPTIONS);
    }
}
