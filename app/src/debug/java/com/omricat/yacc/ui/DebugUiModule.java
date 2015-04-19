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

package com.omricat.yacc.ui;

import com.omricat.yacc.data.di.qualifiers.ApiEndpoint;
import com.omricat.yacc.data.di.qualifiers.DspecGridVisible;
import com.omricat.yacc.data.di.qualifiers.DspecKeylinesVisible;
import com.omricat.yacc.data.di.qualifiers.DspecSpacingsVisible;
import com.omricat.yacc.ui.debug.DebugActivityContainer;

import dagger.Module;
import dagger.Provides;
import info.metadude.android.typedpreferences.BooleanPreference;
import info.metadude.android.typedpreferences.StringPreference;

@Module
public class DebugUiModule {

    @Provides ActivityContainer provideActivityContainer
            (@DspecGridVisible final BooleanPreference dspecGridVisible,
             @DspecKeylinesVisible final BooleanPreference dspecKeylinesVisible,
             @DspecSpacingsVisible final BooleanPreference dspecSpacingsVisible,
             @ApiEndpoint final StringPreference endpoint) {
        return new DebugActivityContainer(dspecGridVisible,
                dspecKeylinesVisible, dspecSpacingsVisible, endpoint);
    }

}
