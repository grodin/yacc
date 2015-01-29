/*
 * Copyright 2015 Omricat Software
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

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public interface DataStore {
    String UTF_8 = "utf-8";

    /**
     * Returns a {@link Reader} which reads from the {@link GcsFilename}
     * passed in the constructor.
     */
    @NotNull Reader getReader();

    /**
     * Returns a {@link Writer} representing the
     * {@link GcsFilename} used to construct the instance.
     *
     * @param fileOptions instance of {@link GcsFileOptions} to use when opening
     *                    the stream, not null.
     * @throws IOException
     */
    @NotNull Writer getWriter(@NotNull GcsFileOptions
                                      fileOptions)
            throws IOException;

    /**
     * Convenience method which returns a {@link Writer} representing the
     * {@link GcsFilename} used to construct the instance, using {@link
     * DefaultDataStore#DEFAULT_FILE_OPTIONS}.
     *
     * @throws IOException
     */
    @NotNull Writer getWriter() throws IOException;
}
