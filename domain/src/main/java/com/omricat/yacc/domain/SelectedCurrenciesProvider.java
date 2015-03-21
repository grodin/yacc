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

package com.omricat.yacc.domain;

import com.omricat.yacc.common.rx.Predicate;
import com.omricat.yacc.data.model.Currency;
import com.omricat.yacc.data.model.CurrencyCode;
import com.omricat.yacc.data.model.CurrencyDataset;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import rx.Observable;
import rx.functions.Func1;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Class to encapsulate getting the currencies currently selected for display
 */
public class SelectedCurrenciesProvider {

    private final Observable<? extends Set<CurrencyCode>>
            selectedCurrenciesObs;
    private final CurrencyDataRequester currencyDataRequester;

    public SelectedCurrenciesProvider(@NotNull final
                                      Observable<? extends Set<CurrencyCode>>
                                              selectedCurrenciesObs,
                                      @NotNull final CurrencyDataRequester
                                              currencyDataRequester) {

        this.selectedCurrenciesObs = checkNotNull(selectedCurrenciesObs);
        this.currencyDataRequester = checkNotNull(currencyDataRequester);
    }

    @NotNull
    public Observable<? extends Collection<Currency>> getCurrencyData() {
        return selectedCurrenciesObs.flatMap(new Func1<Set<CurrencyCode>,
                Observable<? extends Collection<Currency>>>() {


            @Override
            public Observable<? extends Collection<Currency>> call(final
                                                                   Set<CurrencyCode> keys) {
                return currencyDataRequester.request()
                        .flatMap(filterCurrencyData(keys));
            }
        });
    }

    private Func1<CurrencyDataset, Observable<? extends Collection<Currency>>>
    filterCurrencyData(final Set<CurrencyCode> keys) {
        return new Func1<CurrencyDataset, Observable<? extends
                Collection<Currency>>>() {


            @Override
            public Observable<? extends Collection<Currency>>
            call(final CurrencyDataset currencyDataset) {
                return currencyDataset.asObservable()
                        .filter(new Func1<Currency, Boolean>() {
                            @Override
                            public Boolean call(final Currency currency) {
                                return keys.contains(currency.getCode());
                            }
                        }).toList()
                        .filter(new Predicate<List<Currency>>() {
                            @Override
                            public Boolean call(final List<Currency> currencies) {
                                return !currencies.isEmpty();
                            }
                        });
            }
        };
    }


}
