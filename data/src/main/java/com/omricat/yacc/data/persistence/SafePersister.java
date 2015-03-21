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

package com.omricat.yacc.data.persistence;

import org.jetbrains.annotations.NotNull;

import rx.Observable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Implementation of {@link Persister} which wraps another instance,
 * making sure that both the {@link #get(K)} and {@link #put(K,
 * V)} methods return an {@link Observable} which contains at most one element.
 * @param <K>
 * @param <V>
 */
public class SafePersister<K,V> implements Persister<K,V> {

    private final Persister<K,V> wrappedPersister;

    private SafePersister(@NotNull final Persister<K, V> wrappedPersister) {
        this.wrappedPersister = wrappedPersister;
    }

    public static <K, V> SafePersister<K, V> wrap(@NotNull final Persister<K,
            V> wrappedPersister) {
        return new SafePersister<>(checkNotNull(wrappedPersister));
    }

    @NotNull public Observable<V> get(@NotNull final K key) {
        return wrappedPersister.get(key).take(1);
    }

    @NotNull
    public Observable<V> put(@NotNull final K key, @NotNull final V data) {
        return wrappedPersister.put(key, data).take(1);
    }
}
