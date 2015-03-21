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

import com.google.common.collect.ImmutableList;
import com.omricat.yacc.common.rx.Predicate;
import com.omricat.yacc.data.model.Currency;
import com.omricat.yacc.data.model.CurrencyCode;
import com.omricat.yacc.data.persistence.Persister;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import rx.Observable;
import rx.functions.Func1;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Class to encapsulate getting/persisting from/to storage the currency to
 * convert from (the source currency)
 */
public class SourceCurrencyProvider {


    public static final String PERSISTENCE_KEY =
            "last-currency-to-convert-from";

    private final Persister<String, CurrencyCode> sourceCurrencyPersister;
    private final Observable<? extends Collection<Currency>> selectedCurrencies;

    private final Comparator<Currency> comparator = new PopularCurrencies
            .CurrencyByCodePopularsFirstComparator();

    public SourceCurrencyProvider(@NotNull final Persister<String, CurrencyCode>
                                          sourceCurrencyPersister,
                                  @NotNull final Observable<? extends
                                          Collection<Currency>>
                                          selectedCurrencies) {

        this.sourceCurrencyPersister = checkNotNull(sourceCurrencyPersister);
        this.selectedCurrencies = checkNotNull(selectedCurrencies);
    }

    public Observable<Currency> getLatestSourceCurrency() {
        return selectedCurrencies.flatMap(new Func1<Collection<Currency>,
                Observable<Currency>>() {

            @Override public Observable<Currency> call(final
                                                       Collection<Currency>
                                                               currencies) {
                return sourceCurrencyPersister.get(PERSISTENCE_KEY)
                        // check that the currency from the persister
                        // is actually in the set of selected currencies
                        .flatMap(new Func1<CurrencyCode,
                                Observable<? extends Currency>>() {


                            @Override
                            public Observable<? extends Currency> call(final
                                                                       CurrencyCode code) {
                                return Observable.from(currencies)
                                        .filter(new Predicate<Currency>() {
                                            @Override
                                            public Boolean call(final
                                                                Currency
                                                                        currency) {
                                                return currency.getCode()
                                                        .equals(code);
                                            }
                                        });
                            }
                        });
            }
        }).switchIfEmpty(selectedCurrencies
                .flatMap(new Func1<Collection<Currency>
                        , Observable<? extends Currency>>() {

                    @Override
                    public Observable<? extends Currency> call(final
                                                               Collection<Currency>
                                                                       currencies) {
                        if (currencies.isEmpty()) {
                            return Observable.empty();
                        } else {
                            return Observable.just(Collections.min(currencies,
                                    comparator));
                        }
                    }
        }));
    }

    public Observable<Currency> persist(@NotNull final Currency currency) {
        return sourceCurrencyPersister.put(PERSISTENCE_KEY,
                checkNotNull(currency).getCode())
                .flatMap(new Func1<CurrencyCode, Observable<? extends Currency>>() {


                    @Override
                    public Observable<? extends Currency> call(final
                                                               CurrencyCode
                                                                       code) {
                        return Observable.just(currency);
                    }
                });
    }

    static final class PopularCurrencies {

        static final class CurrencyByCodePopularsFirstComparator implements
                Comparator<Currency> {

            @Override public int compare(final Currency o1, final Currency o2) {
                final CurrencyCode code1 = checkNotNull(o1).getCode();
                final CurrencyCode code2 = checkNotNull(o2).getCode();

                final int i1 = PopularCurrencies.asList().indexOf(code1);
                final int i2 = PopularCurrencies.asList().indexOf(code2);

                if (i1 > -1 && i2 > -1) {
                    //Both in the list
                    return i1 - i2;
                } else if (i1 < 0 && i2 < 0) {
                    //Neither in the list
                    return code1.toString().compareTo(code2.toString());
                } else {
                    // One in the list, one not
                    // either i1 >=0 and i1 < 0 or i1 < 0 and i2 >= 0
                    return i2 - i1;
                }
            }

        }

        // CurrencyCodes of 6 most popular currencies
        // TODO: Move list of popular currencies to server
        public final static CurrencyCode USD_CODE = new CurrencyCode("USD");
        public final static CurrencyCode JPY_CODE = new CurrencyCode("JPY");
        public final static CurrencyCode GBP_CODE = new CurrencyCode("GBP");
        public final static CurrencyCode EUR_CODE = new CurrencyCode("EUR");
        public final static CurrencyCode CHF_CODE = new CurrencyCode("CHF");
        public final static CurrencyCode CAD_CODE = new CurrencyCode("CAD");

        private final static List<CurrencyCode> codeList =
                ImmutableList.of(USD_CODE, EUR_CODE, JPY_CODE, GBP_CODE,
                        CHF_CODE, CAD_CODE);

        public static List<CurrencyCode> asList() {
            return codeList;
        }
    }
}
