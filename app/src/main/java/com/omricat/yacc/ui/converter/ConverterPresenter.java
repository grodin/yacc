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

import com.omricat.yacc.data.model.ConvertedCurrency;
import com.omricat.yacc.data.model.Currency;
import com.omricat.yacc.data.persistence.Persister;

import java.util.Collection;

import rx.Observable;

public interface ConverterPresenter {

    public Observable<? extends Collection<ConvertedCurrency>> convertedCurrencies();
    public Observable<Currency> sourceCurrency();

    public final static class Factory {

        private Factory() {throw new AssertionError("No instances allowed");}

        public static ConverterPresenter create(final ConverterView view,
                                                final Persister<String,
                                            Currency> sourceCurrencyPersister) {
            return new ConverterPresenterImpl(view, sourceCurrencyPersister);
        }
    }
}
