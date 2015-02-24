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
import com.omricat.yacc.common.rx.RxSet;
import com.omricat.yacc.data.model.Currency;
import com.omricat.yacc.data.model.CurrencyCode;
import com.omricat.yacc.data.persistence.Persister;

import org.jetbrains.annotations.NotNull;

import rx.Observable;
import rx.functions.Func1;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Class to encapsulate getting/persisting from/to storage the currency to
 * convert from (the source currency)
 */
public class SourceCurrency {

    public static final String PERSISTENCE_KEY = "last-currency-to-convert-from";

    private final Persister<String,Currency> sourceCurrencyPersister;
    private final RxSet<CurrencyCode> selectedCurrencyCodes;

    public SourceCurrency(@NotNull final Persister<String, Currency>
                                  sourceCurrencyPersister,
                          @NotNull final RxSet<CurrencyCode>
                                  selectedCurrencyCodes) {
        this.sourceCurrencyPersister = checkNotNull(sourceCurrencyPersister);
        this.selectedCurrencyCodes = checkNotNull(selectedCurrencyCodes);
    }

    public Observable<Currency> getLatestSourceCurrency() {
        return sourceCurrencyPersister.get(PERSISTENCE_KEY)
                // check that the currency from the persister
                // is actually in the set of selected currencies
                .flatMap(new Func1<Currency, Observable<? extends Currency>>() {
                    @Override
                    public Observable<? extends Currency> call(final Currency
                                                                       currency) {
                        return selectedCurrencyCodes.asObservable()
                                .filter(new Predicate<CurrencyCode>() {
                                    @Override
                                    public Boolean call(final CurrencyCode
                                                                code) {
                                        return code.equals(currency.getCode());
                                    }
                                })
                                // this will just return the currency if the
                                // filtered list is non-empty
                                .flatMap(new Func1<CurrencyCode, Observable<? extends Currency>>() {


                                    @Override
                                    public Observable<? extends Currency>
                                    call(final CurrencyCode code) {
                                        return Observable.just(currency);
                                    }
                                });
                    }
                });
    }

    public Observable<Currency> persist(@NotNull Currency currency) {
        return sourceCurrencyPersister.put(PERSISTENCE_KEY,
                checkNotNull(currency));
    }
}
