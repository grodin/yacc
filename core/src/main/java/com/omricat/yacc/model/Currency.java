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

package com.omricat.yacc.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by jsc on 16/10/14.
 *
 * @author Joseph Cooper
 *         <p/>
 *         A class to represent a currency, and it's value. The value is always
 *         given relative to USD (i.e. value(USD)==1.0) and must be
 *         non-negative.
 */
@JsonSerialize(using = CurrencySerializer.class)
public class Currency {

    public static final String VALUE = "value";
    public static final String CODE = "code";
    public static final String NAME = "name";
    final BigDecimal value;
    final String code;
    final String name;
    final String description;

    /**
     * Constructs an instance of a currency, using the given parameters. The
     * value parameter must be non-negative, and the three string parameters
     * must be non-null.
     * <p/>
     * It is strongly suggested that the code and name parameters should be
     * non-empty and actually match the intended use. The code parameter is
     * <i>not</i> checked for validity.
     *
     * @param value       the value of the currency, relative to USD. Must be
     *                    non-negative amd non-null.
     * @param code        the standard three letter code for this currency (e.g.
     *                    USD, GBP, EUR, etc.) Cannot be null.
     * @param name        the name of this currency. Cannot be null.
     * @param description an optional short description of the currency (e.g.
     *                    "The currency used in the USA"). Cannot be null.
     */
    public Currency(@NotNull final BigDecimal value, @NotNull final String code,
                    @NotNull final String name,
                    @NotNull final String description) {
        checkArgument(checkNotNull(value).compareTo(BigDecimal
                .ZERO) >= 0);
        this.value = checkNotNull(value);
        this.code = checkNotNull(code);
        this.name = checkNotNull(name);
        this.description = checkNotNull(description);
    }

    /**
     * Constructs an instance of a currency, using the given parameters. The
     * value parameter must be non-negative, and the three string parameters
     * must be non-null. Value will be converted to a {@link java.math
     * .BigDecimal}.
     * <p/>
     * It is strongly suggested that the code and name parameters should be
     * non-empty and actually match the intended use. The code parameter is
     * <i>not</i> checked for validity.
     *
     * @param value       the value of the currency, relative to USD. Must be
     *                    non-negative
     * @param code        the standard three letter code for this currency (e.g.
     *                    USD, GBP, EUR, etc.) Cannot be null
     * @param name        the name of this currency. Cannot be null
     * @param description an optional short description of the currency (e.g.
     *                    "The currency used in the USA"). Cannot be null
     */
    public Currency(@NotNull final String value, @NotNull final String code,
                    @NotNull final String name,
                    @NotNull final String description) {
        this(new BigDecimal(value), code, name, description);
    }

    /**
     * Constructs an instance of a currency, using the given parameters. The
     * value parameter must be a string representation of a non-negative
     * decimal, and the code parameter must be non-null. Value will be converted
     * to a {@link java.math .BigDecimal}.
     * <p/>
     * It is strongly suggested that the code and name parameters should be
     * non-empty and actually match the intended use. The code parameter is
     * <i>not</i> checked for validity.
     *
     * @param value the value of the currency, relative to USD. Must be
     *              non-negative
     * @param code  the standard three letter code for this currency (e.g. USD,
     * @param name  the name of this currency. Cannot be null
     */
    @JsonCreator // This constructor will be used by Jackson for deserialisation
    public Currency(@NotNull @JsonProperty( VALUE ) final String value,
                    @NotNull @JsonProperty( CODE ) final String code,
                    @NotNull @JsonProperty( NAME ) final String
                            name) {
        this(new BigDecimal(value), code, name, "");
    }

    /**
     * Get the value of this currency. The value is given by a ratio relative to
     * US Dollars (USD).
     * <p/>
     * E.g. If this currency represents UK pounds (GBP) and one pound is worth 2
     * US dollars, then getValueInUSD() will return 0.5.
     *
     * @return the value of this currency as a float relative to USD
     */
    public BigDecimal getValueInUSD() {
        return value;
    }

    /**
     * Get the standard three letter code for this currency (e.g. USD, GBP,
     * etc.)
     *
     * @return a string containing the three letter code
     */
    public String getCode() {
        return code;
    }

    /**
     * Get the human readable name of this currency (e.g. US Dollars, Pound
     * sterling, Euro, etc.)
     *
     * @return a string containing the human readable name
     */
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
    public String getDescription() {
        return description;
    }

    /**
     * Converts a value in the source currency to a value in the target
     * currency
     *
     * @param source the source currency. Must be non-null.
     * @param target the target currency. Must be non-null.
     * @param value  the value to be converted. This is a value in the source
     *               currency. It should be non-negative.
     * @return the value converted into the target currency
     */
    public static BigDecimal convert(@NotNull final Currency source,
                                     @NotNull final Currency target,
                                     @NotNull final BigDecimal value) {
        checkArgument(value.compareTo(BigDecimal.ZERO) >= 0
                , "Value parameter must be non-negative");

        return value.multiply(source.getValueInUSD()).divide(target
                .getValueInUSD());
    }

    @Override
    public final boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Currency)) return false;

        Currency currency = (Currency) o;

        if (!code.equals(currency.code)) return false;
        if (!description.equals(currency.description)) return false;
        if (!name.equals(currency.name)) return false;
        if (!value.equals(currency.value)) return false;

        return true;
    }

    @Override
    public final int hashCode() {
        int result = value.hashCode();
        result = 31 * result + code.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + description.hashCode();
        return result;
    }


}
