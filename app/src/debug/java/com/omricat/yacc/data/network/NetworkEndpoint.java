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

package com.omricat.yacc.data.network;

import com.omricat.yacc.data.NetworkModule;

import retrofit.Endpoint;
import retrofit.Endpoints;

public enum NetworkEndpoint {

    PRODUCTION("Production", NetworkModule.PRODUCTION_ENDPOINT_URI),
    MOCK_MODE("Mock mode", "mock://");

    private final String name;
    private final String uri;

    NetworkEndpoint(final String name, final String uri) {
        this.name = name;
        this.uri = uri;
    }


    public Endpoint getEndpoint() {
        return Endpoints.newFixedEndpoint(uri, name);
    }

    public static NetworkEndpoint from(String uri) {
        for (NetworkEndpoint value : values()) {
            if (value.uri != null && value.uri.equals(uri)) {
                return value;
            }
        }
        return MOCK_MODE;
    }

    public static boolean isMockMode(String uri) {
        return MOCK_MODE.uri.equals(uri);
    }
}
