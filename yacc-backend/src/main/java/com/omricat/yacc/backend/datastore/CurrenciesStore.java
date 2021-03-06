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

import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.omricat.yacc.backend.Config;

import org.jetbrains.annotations.NotNull;

/**
 * Helper class to abstract over the storage of the currency data
 */
public class CurrenciesStore extends DefaultDataStore {

    private CurrenciesStore() {
        super(new GcsFilename(Config.BUCKET, Config.LATEST_CURRENCY_FILENAME),
                DEFAULT_GCS_SERVICE, DEFAULT_BUFFER_SIZE);
    }

    /**
     * Default factory method, which creates default versions of all
     * dependencies.
     */

    @NotNull public static CurrenciesStore getInstance() {
        return new CurrenciesStore();
    }

}
