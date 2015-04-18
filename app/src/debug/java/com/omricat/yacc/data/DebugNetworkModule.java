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

import android.content.SharedPreferences;

import com.omricat.yacc.data.api.CurrenciesService;
import com.omricat.yacc.data.di.qualifiers.IsMockMode;
import com.omricat.yacc.debug.DebugCurrenciesService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.Endpoint;
import retrofit.MockRestAdapter;
import retrofit.RestAdapter;
import retrofit.android.AndroidMockValuePersistence;

@Module( includes = NetworkModule.class )
public class DebugNetworkModule {

    @Provides @Singleton
    Endpoint provideEndpoint(NetworkEndpoint endpoint) {
        return endpoint.getEndpoint();
    }

    @Provides @Singleton
    MockRestAdapter provideMockRestAdapter(RestAdapter restAdapter,
                                           SharedPreferences preferences) {
        MockRestAdapter mockRestAdapter = MockRestAdapter.from(restAdapter);
        AndroidMockValuePersistence.install(mockRestAdapter, preferences);
        return mockRestAdapter;
    }


    @Provides @Singleton
    public CurrenciesService provideCurrenciesService
            (RestAdapter restAdapter,
             MockRestAdapter mockRestAdapter,
             @IsMockMode boolean isMockMode) {
        if (isMockMode) {
            return mockRestAdapter.create(CurrenciesService.class,
                    new DebugCurrenciesService());
        } else {
            return restAdapter.create(CurrenciesService.class);
        }
    }
}
