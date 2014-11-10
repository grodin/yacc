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

package com.omricat.yacc.data;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Immutable class to hold a collection of {@link com.omricat.yacc.data
 * .Currency} objects and a timestamp indicating when the currencies were last
 * updated.
 */
public class Currencies {

    private final Set<Currency> currencies;

    // lastUpdatedTimestamp is a unix epoch timestamp
    private final long lastUpdatedTimestamp;

    /**
     * Instantiates a currencies list object.
     * @param currencies array of {@link com.omricat.yacc.data.Currency}
     *                   objects. May be empty, but not null.
     * @param lastUpdatedTimestamp unix epoch timestamp indicating the last
     *                             time the list of currencies was updated.
     *                             Must be non-negative.
     */
    @JsonCreator
    public Currencies(@JsonProperty( "currencies" ) @NotNull final Currency[]
                              currencies,
                      @JsonProperty( "timestamp" ) @NotNull final long
                              lastUpdatedTimestamp) {
        checkArgument(lastUpdatedTimestamp >= 0);
        this.currencies = Collections.unmodifiableSet(new HashSet<>(
                Arrays.asList(checkNotNull(currencies))));
        this.lastUpdatedTimestamp = lastUpdatedTimestamp;
    }

    public Set<Currency> getCurrencies() {
        return currencies;
    }

    public long getLastUpdatedTimestamp() {
        return lastUpdatedTimestamp;
    }

    @Override
    public final boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Currencies)) return false;

        Currencies that = (Currencies) o;

        if (lastUpdatedTimestamp != that.lastUpdatedTimestamp) return false;
        if (!currencies.equals(that.currencies))
            return false;

        return true;
    }

    @Override
    public final int hashCode() {
        int result = currencies != null ? currencies.hashCode() : 0;
        result = 31 * result + (int) (lastUpdatedTimestamp ^ (lastUpdatedTimestamp >>> 32));
        return result;
    }
}
