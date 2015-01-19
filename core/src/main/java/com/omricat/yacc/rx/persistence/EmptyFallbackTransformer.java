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

package com.omricat.yacc.rx.persistence;

import org.jetbrains.annotations.NotNull;

import rx.Observable;
import rx.functions.Func0;
import rx.functions.Func1;

import static com.google.common.base.Preconditions.checkNotNull;

public class EmptyFallbackTransformer<T>
        implements Observable.Transformer<T,T> {

    private final Observable<? extends T> fallbackObservable;
    private final Func0<Observable<T>> fallbackObservableFactory;

    private EmptyFallbackTransformer(final Observable<? extends T> fallbackObservable,
                                     final Func0<Observable<T>>
                                             fallbackObservableFactory) {
        this.fallbackObservable = fallbackObservable;
        this.fallbackObservableFactory = fallbackObservableFactory;
    }

    public static <T> EmptyFallbackTransformer<T> getLazyInstance
            (@NotNull
                                                                  final
                                                                  Func0<Observable<T>>
                                                                          fallbackObservableFactory) {
        return new EmptyFallbackTransformer<>(null,
                checkNotNull(fallbackObservableFactory));
    }

    public static <T> EmptyFallbackTransformer<T> getInstance(@NotNull final
                                                              Observable<? extends T>
                                                                      fallbackObservable) {
        return new EmptyFallbackTransformer<>(
                checkNotNull(fallbackObservable),null);
    }

    @Override public Observable<T> call(final Observable<T>
                                                       tObservable) {
        return tObservable.isEmpty().flatMap(new Func1<Boolean,
                Observable<? extends T>>() {

            @Override
            public Observable<? extends T> call(final Boolean isEmpty) {
                if (isEmpty) {
                    if (fallbackObservable != null) {
                        return fallbackObservable;
                    } else {
                        return fallbackObservableFactory.call();
                    }
                } else {
                    return tObservable;
                }
            }
        });
    }
}
