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

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import static org.assertj.core.api.Assertions.assertThat;

public class SelectableCurrencyTest {

    final static Currency USD = new Currency("1.0", "USD", "US Dollar");

    @Test(expected = NullPointerException.class)
    public void testSelect_NullParam() throws Exception {
        SelectableCurrency.select(null, true);
    }

    @Test
    public void testSelect_True() throws Exception {
        boolean ret = SelectableCurrency.select(USD, true).isSelected();
        assertThat(ret).isTrue();
    }

    @Test
    public void testSelect_False() throws Exception {
        boolean ret = SelectableCurrency.select(USD, false).isSelected();
        assertThat(ret).isFalse();
    }

    @Test
    public void testSelectableCurrencyDelegatesToCurrency() throws Exception {
        SelectableCurrency curr = SelectableCurrency.select(USD, true);

        assertThat(curr.getCode()).isEqualTo(USD.getCode());
        assertThat(curr.getRateInUSD()).isEqualTo(USD.getRateInUSD());
        assertThat(curr.getName()).isEqualTo(USD.getName());
        assertThat(curr.getDescription()).isEqualTo(USD.getDescription());
    }

    @Test
    public void testEqualsContract() throws Exception {
        EqualsVerifier.forClass(SelectableCurrency.class)
                .suppress(Warning.NULL_FIELDS)
                .usingGetClass()
                .verify();
    }
}
