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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableSet;
import com.omricat.yacc.model.CurrencyKey;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import rx.Observable;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Class representing a set of currencies. This class does not implement the
 * {@link Set} interface, but it should be thought of as behaving similarly.
 * Duplicate
 */
@JsonSerialize(using = CurrencyKeyRxSetSerializer.class)
class CurrencyKeyRxSet implements RxSet<CurrencyKey> {

    static final String SELECTED_KEYS = "selected-keys";
    private final Set<CurrencyKey> keySet = new LinkedHashSet<>();

    private CurrencyKeyRxSet(@NotNull final Collection<CurrencyKey> keySet) {
        this.keySet.addAll(checkNotNull(keySet));
    }

    @JsonCreator
    static CurrencyKeyRxSet create(@NotNull
                                          @JsonProperty( SELECTED_KEYS )
                                          final Collection<CurrencyKey> keys) {
        return new CurrencyKeyRxSet(keys);
    }

    static CurrencyKeyRxSet create() {
        return create(Collections.<CurrencyKey>emptySet());
    }

    /**
     * Returns an {@link Observable} wrapping an immutable {@link Set}
     * containing the selected keys.
     */
    @Override @NotNull public Observable<Set<CurrencyKey>> get() {
        return Observable.<Set<CurrencyKey>>just(
                ImmutableSet.copyOf(keySet));
    }

    @Override
    @NotNull public Observable<Set<CurrencyKey>> add(@NotNull CurrencyKey
                                                             key) {
        keySet.add(key);
        return get();
    }

    @NotNull @Override
    public Observable<Set<CurrencyKey>> addAll(@NotNull final
                                               Collection<? extends
                                                       CurrencyKey> keys) {
        keySet.addAll(keys);
        return get();
    }


    @Override
    @NotNull public Observable<Set<CurrencyKey>> remove(@NotNull CurrencyKey
                                                                key) {
        keySet.remove(key);
        return get();
    }

    @NotNull @Override
    public Observable<Set<CurrencyKey>> removeAll(@NotNull final
                                                  Collection<? extends
                                                          CurrencyKey> keys) {
        keySet.removeAll(keys);
        return get();
    }

    @NotNull @Override public Observable<CurrencyKey> asObservable() {
        return Observable.from(keySet);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final CurrencyKeyRxSet that = (CurrencyKeyRxSet) o;

        return keySet.equals(that.keySet);
    }

    @Override
    public int hashCode() {
        return keySet.hashCode();
    }

    @Override public String toString() {
        return "CurrencyKeyRxSet{" +
                "keySet=" + keySet +
                '}';
    }
}
