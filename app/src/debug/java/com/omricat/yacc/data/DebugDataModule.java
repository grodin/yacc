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

import com.omricat.yacc.data.di.qualifiers.ApiEndpoint;
import com.omricat.yacc.data.di.qualifiers.DspecGridVisible;
import com.omricat.yacc.data.di.qualifiers.DspecKeylinesVisible;
import com.omricat.yacc.data.di.qualifiers.DspecSpacingsVisible;
import com.omricat.yacc.data.di.qualifiers.IsMockMode;
import com.omricat.yacc.data.network.DebugNetworkModule;
import com.omricat.yacc.data.network.NetworkEndpoint;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import info.metadude.android.typedpreferences.BooleanPreference;
import info.metadude.android.typedpreferences.StringPreference;

@Module(
        includes = {
                DataModule.class,
                DebugNetworkModule.class,
                DebugPersistenceModule.class
        }
)
public final class DebugDataModule {

    private static final boolean DEFAULT_DSPEC_GRID_VISIBLE = false;
    private static final boolean DEFAULT_DSPEC_KEYLINES_VISIBLE = false;
    public static final boolean DEFAULT_DSPEC_SPACINGS_VISIBLE = false;

    @Provides @Singleton @DspecGridVisible
    BooleanPreference provideDspecGridVisible(SharedPreferences prefs) {
        return new BooleanPreference(prefs, "dspec-grid-visible",
                DEFAULT_DSPEC_GRID_VISIBLE);
    }

    @Provides @Singleton @DspecKeylinesVisible
    BooleanPreference provideDspecKeylinesVisible(SharedPreferences prefs) {
        return new BooleanPreference(prefs, "dspec-keylines-visible",
                DEFAULT_DSPEC_KEYLINES_VISIBLE);
    }

    @Provides @Singleton @DspecSpacingsVisible
    BooleanPreference provideDspecSpacingsVisible(SharedPreferences prefs) {
        return new BooleanPreference(prefs, "dspec-spacings-visible",
                DEFAULT_DSPEC_SPACINGS_VISIBLE);
    }

    @Provides @Singleton NetworkEndpoint provideNetworkEndpoint(@ApiEndpoint StringPreference
                                                   endpoint) {
        return NetworkEndpoint.from(endpoint.get());
    }

    @Provides @Singleton @ApiEndpoint
    StringPreference provideNetworkEndpointPreference(SharedPreferences prefs) {
        return new StringPreference(prefs, "api-endpoint", "mock://");
    }

    @Provides @Singleton @IsMockMode
    boolean provideIsMockMode(@ApiEndpoint StringPreference endpoint) {
        return NetworkEndpoint.isMockMode(endpoint.get());
    }

}
