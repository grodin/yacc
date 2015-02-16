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

package com.omricat.yacc.common.rx;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;

import rx.Observable;

/**
 * This interface defines a set with an {@link Observable} API. It should be
 * thought of as an RxJava equivalent to the Java interface {@link Set}.
 *
 * @author Joseph Cooper
 */
public interface RxSet<T> {

    /**
     * Returns an {@link Observable} wrapping a {@link Set} which represents
     * the underlying set of objects.
     */
    @NotNull Observable<? extends Set<T>> get();

    /**
     * Add an object to the set.
     */
    @NotNull Observable<? extends Set<T>> add(@NotNull T object);

    @NotNull Observable<? extends Set<T>> addAll(@NotNull Collection<?
            extends T> keys);

    @NotNull Observable<? extends Set<T>> remove(@NotNull T object);

    @NotNull Observable<? extends Set<T>> removeAll(@NotNull Collection<? extends T> keys);

    @NotNull Observable<T> asObservable();
}
