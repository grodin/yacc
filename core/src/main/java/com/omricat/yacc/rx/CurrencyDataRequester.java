/*
 * Copyright 2014 Omricat Software
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

package com.omricat.yacc.rx;

import com.omricat.yacc.api.CurrenciesService;
import com.omricat.yacc.model.CurrencyDataset;
import com.omricat.yacc.rx.persistence.EmptyFallbackTransformer;
import com.omricat.yacc.rx.persistence.IsDataStalePredicate;
import com.omricat.yacc.rx.persistence.Persister;

import org.jetbrains.annotations.NotNull;

import rx.Observable;
import rx.functions.Func0;
import rx.functions.Func1;

import static com.google.common.base.Preconditions.checkNotNull;

public class CurrencyDataRequester {

    final static String PERSISTENCE_KEY = "currency-data";
    private final Persister<String, CurrencyDataset> persister;
    private final CurrenciesService service;
    private final Func1<CurrencyDataset,Boolean> predicate;

    private CurrencyDataRequester(final Persister<String,
            CurrencyDataset> persister,
                                  final CurrenciesService service, final
    IsDataStalePredicate predicate) {
        this.persister = persister;
        this.service = service;
        this.predicate = predicate;
    }

    public static CurrencyDataRequester create(@NotNull final
                                               Persister<String, CurrencyDataset>
                                                       datasetPersister,
                                               @NotNull final
                                               CurrenciesService service,
                                               @NotNull final
                                               IsDataStalePredicate
                                                       dataStalePredicate) {
        return new CurrencyDataRequester(checkNotNull(datasetPersister),
                checkNotNull(service), checkNotNull(dataStalePredicate));
    }

    @NotNull
    public Observable<CurrencyDataset> request() {

        return persister
                .get(PERSISTENCE_KEY)
                .filter(predicate)
                .compose(EmptyFallbackTransformer.getLazyInstance(
                        new Func0<Observable<CurrencyDataset>>() {

                            @Override public Observable<CurrencyDataset> call() {
                                return service
                                        .getAllCurrencies()
                                        .flatMap(new Func1<CurrencyDataset, Observable<? extends
                                                CurrencyDataset>>() {


                                            @Override
                                            public Observable<? extends CurrencyDataset> call(final
                                                                                              CurrencyDataset
                                                                                                      currencySet) {
                                                return persister.put(PERSISTENCE_KEY, currencySet);
                                            }
                                        });
                            }
                        }));
    }


}
