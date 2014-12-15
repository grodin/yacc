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

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import static org.assertj.core.api.Assertions.assertThat;

public class CurrencySetTest {

    @Test
    public void testEqualsContract() {
        // Fine to suppress null fields warning since constructor forbids nulls
        EqualsVerifier.forClass(CurrencySet.class).suppress(Warning
                .NULL_FIELDS).verify();
    }


    @Test(expected = NullPointerException.class)
    public void testConstructorWithNullCurrencyArray() throws Exception {
        new CurrencySet(null,1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithNegativeTimestamp() throws Exception {
        new CurrencySet(new Currency[] {},-1);
    }

    @Test
    public void testJSONDeserialization() throws Exception {
        final Currency[] currencies = {
                new Currency("1", "USD", "US Dollars")
                , new Currency("3.6732", "GBP", "UK Pound")
                , new Currency("57.34", "EUR", "Euro")
                , new Currency("111.42", "YEN", "Japanese Yen")
        };
        final String json = "{\"currencies\":[{\"code\":\"GBP\"," +
                "\"value\":\"3.6732\",\"name\":\"UK Pound\"}," +
                "{\"code\":\"USD\",\"value\":\"1\",\"name\":\"US Dollars\"}," +
                "{\"code\":\"EUR\",\"value\":\"57.34\",\"name\":\"Euro\"}," +
                "{\"code\":\"YEN\",\"value\":\"111.42\"," +
                "\"name\":\"Japanese Yen\"}],\"timestamp\":1415210401}";
        final CurrencySet expectedCurrencySet = new CurrencySet(currencies,
                1415210401L);
        ObjectMapper objectMapper = new ObjectMapper();
        final CurrencySet currencySetFromJSON = objectMapper.readValue(json,
                CurrencySet
                    .class);
        assertThat(currencySetFromJSON).isEqualTo(expectedCurrencySet);
    }

    @Test
    public void testJSONSerializeThenDeserialize() throws Exception {
        final Currency[] currencies = {
                new Currency("1", "USD", "US Dollars")
                , new Currency("3.6732", "GBP", "UK Pound")
                , new Currency("57.34", "EUR", "Euro")
                , new Currency("111.42", "YEN", "Japanese Yen")
        };
        final CurrencySet expectedCurrencySet = new CurrencySet(currencies,
                1415210401L);
        ObjectMapper objectMapper = new ObjectMapper();
        final String serializedJSON = objectMapper.writeValueAsString
                (expectedCurrencySet);
        final CurrencySet currencySetFromJSON = objectMapper.readValue
                (serializedJSON, CurrencySet.class);
        assertThat(currencySetFromJSON).isEqualTo(expectedCurrencySet);
    }
}
