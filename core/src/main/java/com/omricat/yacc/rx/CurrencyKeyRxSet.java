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
import com.omricat.yacc.rx.persistence.EmptyFallbackTransformer;
import com.omricat.yacc.rx.persistence.Persister;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import rx.Observable;
import rx.functions.Func1;

import static com.google.common.base.Preconditions.checkNotNull;

public class CurrencyKeyRxSet implements RxSet<CurrencyKey> {

    final static String PERSISTENCE_KEY = "selected-currencies";
    private final Set<CurrencyKey> keySet = new HashSet<>();

    private final Persister<String, Set<CurrencyKey>> persister;

    // Function which persists the set of currencies keys and returns it as
    //  an Observable
    private final Func1<Set<CurrencyKey>, Observable<? extends
            Set<CurrencyKey>>>
            persistFunc = new Func1<Set<CurrencyKey>, Observable<? extends
            Set<CurrencyKey>>>() {


        @Override
        public Observable<? extends Set<CurrencyKey>> call(final
                                                           Set<CurrencyKey>
                                                                   currencyKeys) {
            return persister.put(PERSISTENCE_KEY, currencyKeys);
        }
    };

    private final Func1<Set<CurrencyKey>, Observable<? extends Set<CurrencyKey>>>
            storeInMemFunc = new Func1<Set<CurrencyKey>,
            Observable<? extends Set<CurrencyKey>>>() {


        @Override
        public Observable<? extends Set<CurrencyKey>> call(final Set<CurrencyKey>
                                                         currencyKeys) {
            keySet.clear();
            keySet.addAll(currencyKeys);
            return Observable.just(keySet);
        }
    };

    private final EmptyFallbackTransformer<Set<CurrencyKey>>
            emptyFallbackTransformer = EmptyFallbackTransformer.getInstance(
            Observable.just(Collections
                    .<CurrencyKey>emptySet()));

    private CurrencyKeyRxSet(@NotNull final
                             Persister<String, Set<CurrencyKey>>
                                     persister) {
        this.persister = checkNotNull(persister);
    }

    public static CurrencyKeyRxSet create(@NotNull final
                                              Persister<String,
                                                      Set<CurrencyKey>>
                                                      diskPersister) {
        return new CurrencyKeyRxSet(diskPersister);
    }

    @Override @NotNull public Observable<? extends Set<CurrencyKey>> get() {

        return Observable.just(keySet).flatMap(
                new Func1<Set<CurrencyKey>,
                        Observable<? extends Set<CurrencyKey>>>() {
                    @Override
                    public Observable<? extends Set<CurrencyKey>> call(final
                                                             Set<CurrencyKey>
                                                                     currencyKeys) {
                        if (currencyKeys.isEmpty()) {
                            /* If the key set is empty, try to load a set from
                             * the Persister. If that fails then we don't
                             * have a previous set in memory or persistence,
                             * so fallback to an empty set anyway.
                             * In either case, update keyRxSet so it contains
                             * whatever we've got.
                             */
                            return persister.get(PERSISTENCE_KEY)
                                    .compose(emptyFallbackTransformer)
                                    .flatMap(storeInMemFunc);
                        } else {
                            return Observable.just(currencyKeys);
                        }
                    }
                });
    }

    @Override @NotNull
    public Observable<? extends Set<CurrencyKey>> add(@NotNull final CurrencyKey key) {

        final CurrencyKey keyToAdd = checkNotNull(key);
        return applyFuncThenPersist(new Func1<Set<CurrencyKey>,
                Observable<? extends Set<CurrencyKey>>>() {

            @Override
            public Observable<? extends Set<CurrencyKey>> call(final
                                                               Set<CurrencyKey>
                                                                       currencyKeys) {
                keySet.add(keyToAdd);
                return Observable.just(keySet);
            }
        });
    }

    @Override @NotNull
    public Observable<? extends Set<CurrencyKey>> addAll(@NotNull final Collection<?
            extends CurrencyKey> keys) {
        final Collection<? extends CurrencyKey> keysToAdd = checkNotNull(keys);
        return applyFuncThenPersist(new Func1<Set<CurrencyKey>,
                Observable<? extends Set<CurrencyKey>>>() {
            @Override
            public Observable<? extends Set<CurrencyKey>> call(final
                                                               Set<CurrencyKey>
                                                                       currencyKeys) {
                keySet.addAll(keysToAdd);
                return Observable.just(keySet);
            }
        });
    }

    @Override @NotNull
    public Observable<? extends Set<CurrencyKey>> remove(@NotNull final CurrencyKey key) {
        final CurrencyKey keyToRemove = checkNotNull(key);
        return applyFuncThenPersist(new Func1<Set<CurrencyKey>,
                Observable<? extends
                        Set<CurrencyKey>>>() {


            @Override
            public Observable<? extends Set<CurrencyKey>> call(final
                                                               Set<CurrencyKey> currencyKeys) {
                keySet.remove(keyToRemove);
                return Observable.just(keySet);
            }
        });
    }

    @Override @NotNull
    public Observable<? extends Set<CurrencyKey>> removeAll(
            @NotNull final Collection<?
                    extends CurrencyKey> keys) {
        final Collection<? extends CurrencyKey> keysToRemove = checkNotNull(keys);
        return applyFuncThenPersist(new Func1<Set<CurrencyKey>,
                Observable<? extends Set<CurrencyKey>>>() {


            @Override
            public Observable<? extends Set<CurrencyKey>> call(final
                                                               Set<CurrencyKey> currencyKeys) {
                keySet.removeAll(keysToRemove);
                return Observable.just(keySet);
            }
        });
    }

    @Override @NotNull
    public Observable<CurrencyKey> asObservable() {
        return Observable.from(keySet);
    }

    private Observable<? extends Set<CurrencyKey>> applyFuncThenPersist
            (Func1<Set<CurrencyKey>,
                    Observable<? extends Set<CurrencyKey>>> func) {
        return get().flatMap(func).flatMap(persistFunc);
    }

}
