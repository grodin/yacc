/*
 * Copyright 2014 Joseph Cooper
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


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by jsc on 16/10/14.
 *
 * @author Joseph Cooper
 *         <p/>
 *         A class to represent a currency, and it's conversionRate. The
 *         conversionRate is always given relative to USD (i.e.
 *         conversionRate(USD)==1.0) and must be non-negative.
 */
@JsonSerialize(using = CurrencySerializer.class)
public class Currency {

    static final String VALUE = "value";
    static final String CODE = "code";
    static final String NAME = "name";

    /**
     * Constant specifying the scale used in divisions which occur in
     * currency conversions.
     */
    public static final int DIVISION_SCALE = 8;

    /**
     * Constant specifying the rounding mode used in divisions which occur in
     * currency conversions.
     */
    public static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_EVEN;
    public static final Currency USD =
            new Currency("1.0", "USD", "United States Dollar", "");

    private final BigDecimal conversionRate;
    private final CurrencyCode code;
    private final String name;
    private final String description;

    /**
     * Constructs an instance of a currency, using the given parameters. The
     * conversionRate parameter must be non-negative, and the three string
     * parameters must be non-null.
     * <p/>
     * It is strongly suggested that the code and name parameters should be
     * non-empty and actually match the intended use. The code parameter is
     * <i>not</i> checked for validity.
     *
     * @param conversionRate the conversionRate of the currency, relative to
     *                       USD. Must be non-negative and non-null.
     * @param code           {@link CurrencyCode} representing the ISO 4217 code
     *                       for this currency, not null.
     * @param name           the name of this currency, not null.
     * @param description    an optional short description of the currency (e.g.
     *                       "The currency used in the USA"). Cannot be null.
     */
    public Currency(@NotNull final BigDecimal conversionRate,
                    @NotNull final CurrencyCode code,
                    @NotNull final String name,
                    @NotNull final String description) {
        checkArgument(checkNotNull(conversionRate).signum() >= 0);
        this.conversionRate = conversionRate;
        this.code = checkNotNull(code);
        this.name = checkNotNull(name);
        this.description = checkNotNull(description);
    }

    /**
     * Constructs an instance of a currency, using the given parameters. The
     * conversionRate parameter must be non-negative, and the three string
     * parameters must be non-null. Value will be converted to a {@link
     * java.math .BigDecimal}.
     * <p/>
     * It is strongly suggested that the code and name parameters should be
     * non-empty and actually match the intended use. The code parameter is
     * <i>not</i> checked for validity.
     *
     * @param conversionRate the conversionRate of the currency, relative to
     *                       USD. Must be non-negative
     * @param code           the standard three letter code for this currency
     *                       (e.g. USD, GBP, EUR, etc.) Cannot be null
     * @param name           the name of this currency. Cannot be null
     * @param description    an optional short description of the currency (e.g.
     *                       "The currency used in the USA"). Cannot be null
     */
    public Currency(@NotNull final String conversionRate,
                    @NotNull final String code,
                    @NotNull final String name,
                    @NotNull final String description) {
        this(new BigDecimal(conversionRate), new CurrencyCode(code), name,
                description);
    }

    /**
     * Constructs an instance of a currency, using the given parameters. The
     * conversionRate parameter must be a string representation of a
     * non-negative decimal, and the code parameter must be non-null. Value will
     * be converted to a {@link java.math .BigDecimal}.
     * <p/>
     * It is strongly suggested that the code and name parameters should be
     * non-empty and actually match the intended use. The code parameter is
     * <i>not</i> checked for validity.
     *
     * @param conversionRate the conversionRate of the currency, relative to
     *                       USD. Must be non-negative
     * @param code           the standard three letter code for this currency
     *                       (e.g. USD,
     * @param name           the name of this currency. Cannot be null
     */
    @JsonCreator // This constructor will be used by Jackson for deserialisation
    public Currency(@NotNull @JsonProperty(VALUE) final String conversionRate,
                    @NotNull @JsonProperty(CODE) final String code,
                    @NotNull @JsonProperty(NAME) final String name) {
        this(conversionRate, code, name, "");
    }

    /**
     * Get the conversionRate of this currency. The conversionRate is given by a
     * ratio relative to US Dollars (USD).
     * <p/>
     * E.g. If this currency represents UK pounds (GBP) and one pound is worth 2
     * US dollars, then getRateInUSD() will return 0.5.
     *
     * @return the conversionRate of this currency as a float relative to USD
     */
    @NotNull
    public BigDecimal getRateInUSD() {
        return conversionRate;
    }

    /**
     * Get the standard three letter code for this currency (e.g. USD, GBP,
     * etc.)
     *
     * @return a string containing the three letter code
     */
    @NotNull
    public CurrencyCode getCode() {
        return code;
    }

    /**
     * Get the human readable name of this currency (e.g. US Dollars, Pound
     * sterling, Euro, etc.)
     *
     * @return a string containing the human readable name
     */
    @NotNull
    public String getName() {
        return name;
    }


    /**
     * Get a description of this currency (e.g. "This is the currency used in
     * the USA", etc.), if any.
     *
     * @return a string containing a description of this currency, if one was
     * passed to the constructor.
     */
    @NotNull
    public String getDescription() {
        return description;
    }

    /**
     * Converts a conversionRate in the source currency to a conversionRate in
     * the target currency
     *
     * @param source the source currency. Must be non-null.
     * @param target the target currency. Must be non-null.
     * @param value  the conversionRate to be converted. This is a
     *               conversionRate in the source currency. It should be
     *               non-negative.
     * @return the conversionRate converted into the target currency
     */
    @NotNull
    public static BigDecimal convert(@NotNull final Currency source,
                                     @NotNull final Currency target,
                                     @NotNull final BigDecimal value) {
        checkArgument(checkNotNull(value).compareTo(BigDecimal.ZERO) >= 0
                , "Value parameter must be non-negative");

        return value.multiply(conversionRatio(checkNotNull(source),
                checkNotNull(target)));
    }

    @NotNull
    public static BigDecimal conversionRatio(@NotNull final Currency source,
                                             @NotNull final Currency target) {
        return source.getRateInUSD().divide(target.getRateInUSD(), DIVISION_SCALE,

                ROUNDING_MODE);
    }

    @Override
    public final boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Currency)) return false;

        Currency currency = (Currency) o;

        if (!code.equals(currency.code)) return false;
        if (!description.equals(currency.description)) return false;
        if (!name.equals(currency.name)) return false;
        if (!conversionRate.equals(currency.conversionRate)) return false;

        return true;
    }

    @Override
    public final int hashCode() {
        return Objects.hashCode(conversionRate, code, name, description);
    }

    @Override public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("conversionRate", conversionRate)
                .add("code", code)
                .add("name", name)
                .add("description", description.isEmpty() ? null : description)
                .toString();
    }
}
