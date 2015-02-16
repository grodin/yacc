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

package com.omricat.yacc.data.model;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

import static com.google.common.base.Preconditions.checkNotNull;

public class SelectableCurrency {

    private final boolean isSelected;
    private final Currency currency;

    private SelectableCurrency(@NotNull final Currency currency,
                               final boolean isSelected) {
        this.currency = currency;
        this.isSelected = isSelected;
    }

    public static SelectableCurrency select(@NotNull final Currency currency,
                                            final boolean selected) {
        return new SelectableCurrency(checkNotNull(currency), selected);
    }

    public boolean isSelected() {
        return isSelected;
    }

    @NotNull
    public BigDecimal getRateInUSD() {
        return currency.getRateInUSD();
    }

    @NotNull
    public CurrencyCode getCode() {
        return currency.getCode();
    }

    @NotNull public String getName() {
        return currency.getName();
    }

    @NotNull public String getDescription() {
        return currency.getDescription();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final SelectableCurrency that = (SelectableCurrency) o;

        return isSelected == that.isSelected
                && Objects.equal(this.currency, that.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(currency, isSelected);
    }

    @Override public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("isSelected", isSelected)
                .add("currency", currency)
                .toString();
    }
}
