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

import com.google.common.collect.ImmutableSet;
import com.omricat.yacc.model.CurrencyCode;
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

public class CurrencyKeyRxSet implements RxSet<CurrencyCode> {

    final static String PERSISTENCE_KEY = "selected-currencies";
    private final Set<CurrencyCode> keySet = new HashSet<>();

    private final Persister<String, Set<CurrencyCode>> persister;

    // Function which persists the set of currencies keys and returns it as
    //  an Observable
    private final Func1<Set<CurrencyCode>, Observable<? extends
            Set<CurrencyCode>>>
            persistFunc = new Func1<Set<CurrencyCode>, Observable<? extends
            Set<CurrencyCode>>>() {


        @Override
        public Observable<? extends Set<CurrencyCode>> call(final
                                                           Set<CurrencyCode>
                                                                            currencyCodes) {
            return persister.put(PERSISTENCE_KEY, currencyCodes);
        }
    };

    private final Func1<Set<CurrencyCode>, Observable<? extends
            Set<CurrencyCode>>>
            storeInMemFunc = new Func1<Set<CurrencyCode>,
            Observable<? extends Set<CurrencyCode>>>() {


        @Override
        public Observable<? extends Set<CurrencyCode>> call(final
                                                           Set<CurrencyCode>
                                                                            currencyCodes) {
            keySet.clear();
            keySet.addAll(currencyCodes);
            return keySetObservable(keySet);
        }
    };

    private final EmptyFallbackTransformer<Set<CurrencyCode>>
            emptyFallbackTransformer = EmptyFallbackTransformer.getInstance(
            Observable.just(Collections
                    .<CurrencyCode>emptySet()));

    private CurrencyKeyRxSet(@NotNull final
                             Persister<String, Set<CurrencyCode>>
                                     persister) {
        this.persister = checkNotNull(persister);
    }

    public static CurrencyKeyRxSet create(@NotNull final
                                          Persister<String,
                                                  Set<CurrencyCode>>
                                                  diskPersister) {
        return new CurrencyKeyRxSet(diskPersister);
    }

    @Override @NotNull public Observable<? extends Set<CurrencyCode>> get() {

        return keySetObservable(keySet).flatMap(
                new Func1<Set<CurrencyCode>,
                        Observable<? extends Set<CurrencyCode>>>() {
                    @Override
                    public Observable<? extends Set<CurrencyCode>> call(final
                                                                       Set<CurrencyCode>
                                                                                                    currencyCodes) {
                        if (currencyCodes.isEmpty()) {
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
                            return keySetObservable(currencyCodes);
                        }
                    }
                });
    }

    @Override @NotNull
    public Observable<? extends Set<CurrencyCode>> add(@NotNull final
                                                           CurrencyCode key) {
        return addAll(ImmutableSet.of(checkNotNull(key)));
    }

    @Override @NotNull
    public Observable<? extends Set<CurrencyCode>> addAll(@NotNull final
                                                             Collection<?
            extends CurrencyCode> keys) {
        final Collection<? extends CurrencyCode> keysToAdd = checkNotNull(keys);
        return applyFuncThenPersist(new Func1<Set<CurrencyCode>,
                Observable<? extends Set<CurrencyCode>>>() {
            @Override
            public Observable<? extends Set<CurrencyCode>> call(final
                                                               Set<CurrencyCode>
                                                                                    currencyCodes) {
                keySet.addAll(keysToAdd);
                return keySetObservable(keySet);
            }
        });
    }

    @Override @NotNull
    public Observable<? extends Set<CurrencyCode>> remove(@NotNull final
                                                              CurrencyCode key) {
        return removeAll(ImmutableSet.of(checkNotNull(key)));
    }

    @Override @NotNull
    public Observable<? extends Set<CurrencyCode>> removeAll(
            @NotNull final Collection<?
                    extends CurrencyCode> keys) {
        final Collection<? extends CurrencyCode> keysToRemove
                = checkNotNull(keys);
        return applyFuncThenPersist(new Func1<Set<CurrencyCode>,
                Observable<? extends Set<CurrencyCode>>>() {


            @Override
            public Observable<? extends Set<CurrencyCode>> call(final
                                                               Set<CurrencyCode> currencyCodes) {
                keySet.removeAll(keysToRemove);
                return keySetObservable(keySet);
            }
        });
    }

    @Override @NotNull
    public Observable<CurrencyCode> asObservable() {
        return Observable.from(keySet);
    }

    private Observable<? extends Set<CurrencyCode>> keySetObservable(final
                                                                    Set<CurrencyCode> keys) {
        return Observable.just(ImmutableSet.copyOf(keys));
    }

    private Observable<? extends Set<CurrencyCode>> applyFuncThenPersist
            (Func1<Set<CurrencyCode>,
                    Observable<? extends Set<CurrencyCode>>> func) {
        return get().flatMap(func).flatMap(persistFunc);
    }
}
