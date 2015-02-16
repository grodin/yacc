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

package com.omricat.yacc.data.persistence;

import org.jetbrains.annotations.NotNull;

import rx.Observable;

/**
 * An interface to represent storing and retrieving simple objects to/from
 * somewhere.
 * <p/>
 * It is required as part of the contract of this interface that calls to
 * {@link #put(K,V)} <emph>must</emph> return the same value as was passed
 * in, wrapped in an Observable.
 *
 * @author Joseph Cooper
 */
public interface Persister<K, V> {

    /**
     * Return an {@link Observable} which will
     * contain the value corresponding to the parameter {@code key}, if it
     * exists. Otherwise, the wrapped {@code Observable} will be empty (i.e.
     * will complete immediately without emitting anything).
     *
     * @param key the key to use to lookup the value, not null.
     * @return an {@code Observable<Optional>} containing the value if it exists
     */
    @NotNull Observable<V> get(@NotNull final K key);

    /**
     * Stores a value under a key and then returns the value wrapped in an
     * {@link Observable}.
     * <p/>
     * Note that {@code null} may not be used as a value or key.
     *
     * @param key  the key to use to store the value, not null.
     * @param data the value to be stored, not null.
     * @return an {@code Observable } wrapping the value
     */
    @NotNull Observable<V> put(@NotNull final K key, @NotNull final V data);

}
