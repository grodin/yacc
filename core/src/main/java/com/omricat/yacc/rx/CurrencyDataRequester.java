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
import com.omricat.yacc.model.CurrencySet;

import org.jetbrains.annotations.NotNull;

import rx.Observable;
import rx.functions.Func1;

import static com.google.common.base.Preconditions.checkNotNull;

public class CurrencyDataRequester {

    private final Persister<String, CurrencySet> memPersister;
    private final Persister<String, CurrencySet> diskPersister;
    private final CurrenciesService service;

    public CurrencyDataRequester(@NotNull final Persister<String,
            CurrencySet> memPersister,
                                 @NotNull final Persister<String,
                                         CurrencySet> diskPersister,
                                 @NotNull final CurrenciesService service) {
        this.memPersister = checkNotNull(memPersister);
        this.diskPersister = checkNotNull(diskPersister);
        this.service = checkNotNull(service);
    }

    @NotNull public Observable<CurrencySet> request(@NotNull final String key) {
        final Observable<CurrencySet> networkFlow = service.getAllCurrencies()
                .flatMap(new Func1<CurrencySet, Observable<? extends
                        CurrencySet>>() {


                    @Override
                    public Observable<? extends CurrencySet> call(final
                                                                  CurrencySet
                                                                          currencySet) {
                        return diskPersister.put(key, currencySet);
                    }
                })
                .flatMap(new Func1<CurrencySet, Observable<? extends
                        CurrencySet>>() {


                    @Override
                    public Observable<? extends CurrencySet> call(final
                                                                  CurrencySet
                                                                          currencySet) {
                        return memPersister.put(key, currencySet);
                    }
                });

        final Observable<CurrencySet> diskFlow = diskPersister
                .get(key)
                .flatMap(OptionalObservableFunc.of(networkFlow))
                .flatMap(IsDataStaleFunc.create(networkFlow,
                        IsDataStaleFunc.CURRENT_EPOCH_FUNC));

        return memPersister.get(key)
                .flatMap(OptionalObservableFunc.of(diskFlow))
                .flatMap(IsDataStaleFunc.create(diskFlow,
                        IsDataStaleFunc.CURRENT_EPOCH_FUNC));
    }


}
