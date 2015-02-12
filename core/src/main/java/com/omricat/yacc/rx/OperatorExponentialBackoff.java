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

package com.omricat.yacc.rx;


import com.omricat.yacc.util.tuples.Pair;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Scheduler;
import rx.functions.Func1;
import rx.functions.Func2;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class OperatorExponentialBackoff<T>
        implements Observable.Transformer<T, T> {

    private final Scheduler scheduler;
    private final int maxRetries;


    public OperatorExponentialBackoff(final int maxRetries,
                                      @NotNull final Scheduler
                                              scheduler) {
        checkArgument(maxRetries >= 0, "maxRetries must be at least 0");
        this.maxRetries = maxRetries;
        this.scheduler = checkNotNull(scheduler);
    }

    @NotNull
    @Override public Observable<T> call(final Observable<T> tObservable) {
        return tObservable.retryWhen(new Func1<Observable<? extends Throwable>,
                Observable<?>>() {


            @Override
            public Observable<?> call(final Observable<? extends Throwable>
                                              observable) {
                return observable
                        .zipWith(Observable.range(0, maxRetries + 2),
                                new Func2<Throwable, Integer,
                                        Pair<Throwable, Integer>>() {

                                    @Override
                                    public Pair<Throwable, Integer> call
                                            (final Throwable t,
                                                                         final Integer i) {
                                        return Pair.of(t, i);
                                    }
                                })
                        .flatMap(new Func1<Pair<Throwable, Integer>, Observable<?>>() {
                            @Override
                            public Observable<Long> call(final Pair<Throwable, Integer> ti) {
                                if (ti.second < maxRetries) {
                                    return Observable.timer(delay(ti.second),
                                            TimeUnit.MILLISECONDS, scheduler);
                                } else {
                                    return Observable.error(ti.first);
                                }
                            }
                        });
            }
        }, scheduler);
    }

    private static long delay(final int i) {
        return TimeUnit.SECONDS.toMillis(1L << i );
    }
}
