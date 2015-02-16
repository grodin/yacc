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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class ConvertedCurrency {

    private final Currency targetCurrency;
    private final Currency sourceCurrency;
    private final BigDecimal conversionRate;
    private final BigDecimal valueToConvert;
    private final BigDecimal convertedValue;

    private ConvertedCurrency(final Currency targetCurrency,
                              final Currency sourceCurrency,
                              final BigDecimal valueToConvert) {
        this.targetCurrency = targetCurrency;
        this.sourceCurrency = sourceCurrency;
        this.valueToConvert = valueToConvert;

        this.conversionRate = Currency.conversionRatio(sourceCurrency,
                targetCurrency);

        convertedValue = Currency.convert(this.sourceCurrency,
                this.targetCurrency,
                this.valueToConvert);
    }

    public static ConvertedCurrency convertFromTo(@NotNull final Currency
                                                          sourceCurrency,
                                                  @NotNull final Currency
                                                          targetCurrency,
                                                  @NotNull final BigDecimal
                                                          valueToConvert) {
        checkArgument(valueToConvert.signum() >= 0);
        return new ConvertedCurrency(checkNotNull(targetCurrency),
                checkNotNull(sourceCurrency), checkNotNull(valueToConvert));
    }


    @NotNull
    public CurrencyCode getCode() {
        return targetCurrency.getCode();
    }

    @NotNull public String getName() {
        return targetCurrency.getName();
    }

    @NotNull public String getDescription() {
        return targetCurrency.getDescription();
    }

    @NotNull public BigDecimal getConversionRate() {
        return conversionRate;
    }

    @NotNull public BigDecimal getConvertedValue() {
        return convertedValue;
    }

    @Override
    public final boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final ConvertedCurrency that = (ConvertedCurrency) o;

        if (!sourceCurrency.equals(that.sourceCurrency)) return false;
        if (!targetCurrency.equals(that.targetCurrency)) return false;
        if (!valueToConvert.equals(that.valueToConvert)) return false;

        return true;
    }

    @Override
    public final int hashCode() {
        return Objects.hashCode(targetCurrency, sourceCurrency, valueToConvert);
    }

    @Override public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("valueToConvert", valueToConvert)
                .add("sourceCurrency", sourceCurrency)
                .add("targetCurrency", targetCurrency)
                .toString();
    }
}
