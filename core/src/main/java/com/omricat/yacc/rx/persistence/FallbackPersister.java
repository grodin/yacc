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

package com.omricat.yacc.rx.persistence;

import com.google.common.base.Optional;

import org.jetbrains.annotations.NotNull;

import rx.Observable;
import rx.functions.Func1;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * An implementation of {@link Persister} which wraps two {@code Persister}
 * instances. On a call to {@link #get(K)}, this implementation delegates the
 * call to the first {@code Persister}, and returns it's value if there is one.
 * Otherwise, it delegates the call to the second {@code Persister} and returns
 * whatever is returned by that.
 * <p/>
 * An optional predicate (an instance of {@link Func1}{@code &lt;V,} {@link
 * Boolean}{@code &gt;}) can be passed to the constructor {@link
 * #FallbackPersister(Persister, Persister, Func1)} which will be called on the
 * value returned in the {@code get()} call on the first {@code Persister}. If
 * it passes, the value will be passed on. If it fails, the call will fall back
 * to the second {@code Persister}.
 * <p/>
 * Calls to {@link #put(K, V)} will simply call {@link Persister#put(K, V)} on
 * both {@code Persister} instances with the same value as passed to this
 * implementation.
 *
 * @author Joseph Cooper
 */
public final class FallbackPersister<K, V> implements Persister<K, V> {

    private final Persister<K, V> firstPersister;
    private final Persister<K, V> secondPersister;
    private final Func1<V, Boolean> predicate;

    public FallbackPersister(@NotNull final Persister<K, V> firstPersister,
                             @NotNull final Persister<K, V> secondPersister,
                             final Func1<V, Boolean> predicate) {
        this.predicate = predicate;
        this.firstPersister = checkNotNull(firstPersister);
        this.secondPersister = checkNotNull(secondPersister);
    }

    public FallbackPersister(@NotNull final Persister<K, V> firstPersister,
                             @NotNull final Persister<K, V> secondPersister) {
        this(firstPersister, secondPersister, null);
    }

    @NotNull @Override
    public Observable<Optional<V>> get(@NotNull final K key) {
        final Observable<Optional<V>> second =
                secondPersister.get(key);

        return firstPersister.get(key)
                .flatMap(new Func1<Optional<V>, Observable<Optional<V>>>() {

                    @Override
                    public Observable<Optional<V>> call(final Optional<V>
                                                                vOptional) {
                        if (vOptional.isPresent()) {
                            return checkPredicate(predicate, vOptional.get(),
                                    Observable.just(vOptional), second);
                        } else {
                            return second;
                        }
                    }
                });
    }

    @NotNull @Override
    public Observable<V> put(@NotNull final K key, @NotNull final V data) {
        return firstPersister.put(key, data).flatMap(new Func1<V,
                Observable<? extends V>>() {

            @Override public Observable<? extends V> call(final V v) {
                return secondPersister.put(key, v);
            }
        });
    }


    private static <V, T> T checkPredicate(final Func1<V, Boolean> predicate,
                                           final V v, final T first,
                                           final T second) {
        if (predicate == null) {
            return first;
        } else if (predicate.call(v)) {
            return first;
        } else {
            return second;
        }
    }
}
