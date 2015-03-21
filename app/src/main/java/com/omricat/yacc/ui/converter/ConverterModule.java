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

package com.omricat.yacc.ui.converter;

import com.omricat.yacc.data.model.Currency;
import com.omricat.yacc.di.qualifiers.SelectedCurrencies;
import com.omricat.yacc.domain.SourceCurrencyProvider;

import java.util.Collection;

import dagger.Module;
import dagger.Provides;
import rx.Observable;

@Module
public class ConverterModule {

    private final ConverterView view;

    public ConverterModule(final ConverterView view) {
        this.view = view;
    }

    @ConverterScope
    @Provides
    ConverterPresenter providePresenter(final SourceCurrencyProvider sourceCurrencyProvider,
                                        @SelectedCurrencies final
                                        Observable<? extends
                                                Collection<Currency>>
                                                currencies) {
        return new ConverterPresenterImpl(view, sourceCurrencyProvider, currencies);
    }

}
