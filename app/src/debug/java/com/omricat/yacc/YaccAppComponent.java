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

package com.omricat.yacc;

import com.omricat.yacc.data.DebugNetworkModule;
import com.omricat.yacc.data.DebugPersistenceModule;
import com.omricat.yacc.domain.DebugDomainModule;
import com.omricat.yacc.ui.converter.ConverterModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(
        dependencies = {},
        modules = {YaccAppModule.class,
                ConverterModule.class,
                DebugPersistenceModule.class,
                DebugNetworkModule.class,
                DebugDomainModule.class}
)
public interface YaccAppComponent extends YaccAppGraph {

    final static class Initializer {
        static YaccAppComponent init(YaccApp app) {
            return Dagger_YaccAppComponent.builder()
                    .yaccAppModule(new YaccAppModule(app))
                    .build();
        }
    }


}
