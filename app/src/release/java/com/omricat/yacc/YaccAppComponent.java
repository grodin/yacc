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

import com.omricat.yacc.data.ReleaseDataModule;
import com.omricat.yacc.di.scopes.AppScope;
import com.omricat.yacc.domain.ReleaseDomainModule;
import com.omricat.yacc.ui.ReleaseUiModule;
import com.omricat.yacc.ui.converter.ConverterModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(
        dependencies = {},
        modules = {
                YaccAppModule.class,
                ConverterModule.class,
                ReleaseDataModule.class,
                ReleaseDomainModule.class,
                ReleaseUiModule.class
        }
)
public interface YaccAppComponent extends YaccAppGraph {

    final static class Initializer {
        static YaccAppComponent init(YaccApp app) {
            return DaggerYaccAppComponent.builder()
                    .yaccAppModule(new YaccAppModule(app))
                    .build();
        }

        private Initializer() { /* No instances */ }
    }
}


