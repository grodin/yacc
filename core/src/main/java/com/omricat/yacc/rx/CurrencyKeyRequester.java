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

import com.omricat.yacc.model.CurrencyKey;
import com.omricat.yacc.rx.persistence.OptionalObservableFunc;
import com.omricat.yacc.rx.persistence.Persister;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import rx.Observable;
import rx.functions.Func1;

import static com.google.common.base.Preconditions.checkNotNull;

public class CurrencyKeyRequester implements RxSet<CurrencyKey> {

    final static String PERSISTENCE_KEY = "selected-currencies";
    private final Func1<Set<CurrencyKey>, Observable<? extends
            Set<CurrencyKey>>> getKeysFunc;
    private CurrencyKeyRxSet keySet = CurrencyKeyRxSet.create();
    private final Persister<String, Set<CurrencyKey>> diskPersister;

    // Function which persists the set of currencies keys and returns it as
    //  an Observable
    private final Func1<Set<CurrencyKey>, Observable<? extends
            Set<CurrencyKey>>> persistFunc;

    private CurrencyKeyRequester(@NotNull final
                                 Persister<String, Set<CurrencyKey>>
                                         diskPersister) {
        this.diskPersister = checkNotNull(diskPersister);
        persistFunc = new Func1<Set<CurrencyKey>, Observable<? extends Set<CurrencyKey>>>() {


            @Override
            public Observable<? extends Set<CurrencyKey>> call(final
                                                               Set<CurrencyKey> currencyKeys) {
                return diskPersister.put(PERSISTENCE_KEY, currencyKeys);
            }
        };
        getKeysFunc = new Func1<Set<CurrencyKey>, Observable<? extends
                        Set<CurrencyKey>>>() {


            @Override
            public Observable<? extends Set<CurrencyKey>> call(final
                                                               Set<CurrencyKey>
                                                                       currencyKeys) {
                return keySet.get();
            }
        };
    }

    public static CurrencyKeyRequester create(@NotNull final
                                              Persister<String,
                                                      Set<CurrencyKey>>
                                                      diskPersister) {
        return new CurrencyKeyRequester(diskPersister);
    }

    @Override @NotNull public Observable<Set<CurrencyKey>> get() {
        final Observable<Set<CurrencyKey>> diskflow
                = diskPersister.get(PERSISTENCE_KEY)
                .flatMap(OptionalObservableFunc.of(Observable.just
                        (Collections.<CurrencyKey>emptySet()))); // unwrap Optional


        return keySet.get().flatMap(
                new Func1<Set<CurrencyKey>,
                        Observable<Set<CurrencyKey>>>() {
                    @Override
                    public Observable<Set<CurrencyKey>> call(final
                                                             Set<CurrencyKey>
                                                                     currencyKeys) {
                        if (currencyKeys.isEmpty()) {
                            return diskflow;
                        } else {
                            return Observable.just(currencyKeys);
                        }
                    }
                });
    }

    @Override @NotNull
    public Observable<Set<CurrencyKey>> add(@NotNull final CurrencyKey key) {
      return keySet.add(key).flatMap(persistFunc).flatMap(getKeysFunc);
    }

    @NotNull @Override
    public Observable<Set<CurrencyKey>> addAll(@NotNull final Collection<?
            extends CurrencyKey> keys) {
        return keySet.addAll(keys).flatMap(persistFunc).flatMap(getKeysFunc);
    }

    @Override @NotNull
    public Observable<Set<CurrencyKey>> remove(
            @NotNull final CurrencyKey key) {
        return keySet.remove(key).flatMap(persistFunc).flatMap(getKeysFunc);
    }

    @NotNull @Override
    public Observable<Set<CurrencyKey>> removeAll(
            @NotNull final Collection<?
                    extends CurrencyKey> keys) {
        return keySet.removeAll(keys).flatMap(persistFunc).flatMap(getKeysFunc);
    }

    @NotNull @Override public Observable<CurrencyKey> asObservable() {
        return keySet.asObservable();
    }

}
