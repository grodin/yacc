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

package com.omricat.yacc.model;

import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.CharMatcher;

import org.jetbrains.annotations.NotNull;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Class with value semantics representing currency identifiers as specified by
 * ISO 4217.
 * <p/>
 * This class does very basic validation. A {@link String} is valid if the
 * following are true: <ol> <li>The String is not null.</li> <li>All characters
 * are in the range 'A' to 'Z', i.e. all uppercase Roman characters</li> <li>The
 * length of the string is exactly 3</li>  </ol>
 * <p/>
 * This class does <emph>not</emph> check that the code is actually one of
 * the codes specified by ISO 4217, merely that it looks like it could be.
 * <p/>
 * This class has value semantics, i.e. two {@code CurrencyKey} instances are
 * equal according to {@link #equals(Object)} and {@link #hashCode()} if and
 * only if they have equal keys.
 */
public final class CurrencyCode {

    @NotNull
    private final String key;

    /**
     * Creates a new instance with the passed {@code String} as key.
     * @param key {@code String} to use as key
     */
    public CurrencyCode(@NotNull final String key) {
        checkNotNull(key);
        checkArgument(CharMatcher.inRange('A', 'Z').matchesAllOf(key));
        checkArgument(key.length() == 3);
        this.key = checkNotNull(key);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final CurrencyCode that = (CurrencyCode) o;

        return toString().equals(that.toString());

    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @JsonValue
    @Override public String toString() {
        return key;
    }

}
