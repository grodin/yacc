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

package com.omricat.yacc.data;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.Endpoint;
import retrofit.RestAdapter;

@Module
public class NetworkModule {

    public static final String PRODUCTION_ENDPOINT_URI =
            "https://yacc-backend.appspot.com/latest";

    @Provides @Singleton RestAdapter provideRestAdapter(Endpoint endpoint) {
        return new RestAdapter.Builder()
                .setEndpoint(endpoint)
                .build();
    }


}
