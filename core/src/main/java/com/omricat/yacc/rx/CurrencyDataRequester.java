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

import org.jetbrains.annotations.NotNull;

import rx.Observable;
import rx.functions.Func1;

import static com.google.common.base.Preconditions.checkNotNull;

public class CurrencyDataRequester {

    private final Persister<String, CurrencyDataset> diskPersister;
    private final CurrenciesService service;

    private CurrencyDataRequester(@NotNull final Persister<String,
            CurrencyDataset> diskPersister,
                                  @NotNull final CurrenciesService service) {
        this.diskPersister = checkNotNull(diskPersister);
        this.service = checkNotNull(service);
    }

    public static CurrencyDataRequester create(@NotNull final Persister<String,
            CurrencyDataset>
                                                       diskPersister,
                                               @NotNull final CurrenciesService service) {
        return new CurrencyDataRequester(diskPersister, service);
    }

    @NotNull public Observable<CurrencyDataset> request(@NotNull final String key) {
        final Observable<CurrencyDataset> networkFlow = service.getAllCurrencies()
                .flatMap(new Func1<CurrencyDataset, Observable<? extends
                        CurrencyDataset>>() {


                    @Override
                    public Observable<? extends CurrencyDataset> call(final
                                                                                          CurrencyDataset
                                                                          currencySet) {
                        return diskPersister.put(key, currencySet);
                    }
                });

        return diskPersister
                .get(key)
                .flatMap(OptionalObservableFunc.of(networkFlow))
                .flatMap(IsDataStaleFunc.create(networkFlow,
                        IsDataStaleFunc.CURRENT_EPOCH_FUNC));
    }


}
