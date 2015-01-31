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

package com.omricat.yacc.model;

import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import static org.assertj.core.api.Assertions.assertThat;

public class ConvertedCurrencyTest {

    final static Currency USD = new Currency("13.0", "USD", "US Dollar");
    final static Currency GBP = new Currency("17.0", "GBP",
            "British Pound Sterling");
    private final int DIVISION_SCALE = 8;

    @Test( expected = NullPointerException.class )
    public void testCreation_1stParamNull() throws Exception {
        ConvertedCurrency.convertFromTo(null, GBP, new BigDecimal(1));
    }

    @Test( expected = NullPointerException.class )
    public void testCreation_2ndParamNull() throws Exception {
        ConvertedCurrency.convertFromTo(USD, null, new BigDecimal(1));
    }

    @Test( expected = NullPointerException.class )
    public void testCreation_3rdParamNull() throws Exception {
        ConvertedCurrency.convertFromTo(USD, GBP, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreation_ValueNegative() throws Exception {
        ConvertedCurrency.convertFromTo(USD, GBP, new BigDecimal(-1));
    }

    @Test
    public void testConvertedCurrencyDelegatesToCurrency() throws Exception {
        ConvertedCurrency curr = ConvertedCurrency.convertFromTo(GBP, USD,
                new BigDecimal(1));

        assertThat(curr.getCode()).isEqualTo(USD.getCode());
        assertThat(curr.getName()).isEqualTo(USD.getName());
        assertThat(curr.getDescription()).isEqualTo(USD.getDescription());
    }

    @Test
    public void testGetConversionRate() throws Exception {
        final BigDecimal TEN = new BigDecimal(10);
        final BigDecimal THIRTEEN = new BigDecimal(13);
        final BigDecimal SEVENTEEN = new BigDecimal(17);

        ConvertedCurrency curr = ConvertedCurrency.convertFromTo(USD, GBP, TEN);

        assertThat(curr.getConversionRate()).isEqualByComparingTo(THIRTEEN
                .divide(SEVENTEEN, DIVISION_SCALE, RoundingMode.HALF_EVEN));
    }

    @Test
    public void testGetConvertedValue() throws Exception {
        final BigDecimal TEN = new BigDecimal(10);
        final BigDecimal THIRTEEN = new BigDecimal(13);
        final BigDecimal SEVENTEEN = new BigDecimal(17);

        ConvertedCurrency curr = ConvertedCurrency.convertFromTo(USD, GBP, TEN);

        assertThat(curr.getConvertedValue()).isEqualByComparingTo(TEN.multiply(
                THIRTEEN.divide(SEVENTEEN,
                        DIVISION_SCALE, RoundingMode.HALF_EVEN)
        ));

    }

    @Test
    public void testEqualsContract() throws Exception {
        EqualsVerifier.forClass(ConvertedCurrency.class)
                .usingGetClass()
                .suppress(Warning.NULL_FIELDS)
                .verify();
    }
}
