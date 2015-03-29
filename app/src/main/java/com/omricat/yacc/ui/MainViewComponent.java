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

import com.omricat.yacc.YaccApp;
import com.omricat.yacc.YaccAppComponent;
import com.omricat.yacc.di.scopes.MainViewScope;
import com.omricat.yacc.ui.converter.ConverterPresenter;

import dagger.Component;

@MainViewScope
@Component(
        dependencies = {YaccAppComponent.class},
        modules = {MainViewModule.class}
)
public interface MainViewComponent {

    void inject(YaccMainActivity activity);

    public ConverterPresenter converterPresenter();

    final static class Initializer {
        static MainViewComponent init(YaccMainActivity mainView) {
            return Dagger_MainViewComponent.builder()
                    .mainViewModule(new MainViewModule(mainView))
                    .yaccAppComponent(YaccApp.from(mainView).component())
                    .build();
        }
    }

}
