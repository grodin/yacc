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

package com.omricat.yacc.data.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;

import org.junit.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import static org.assertj.core.api.Assertions.assertThat;

public class CurrencyDatasetTest {

    private final Currency usd = new Currency("1", "USD", "US Dollars");
    private final Currency gbp = new Currency("3.6732", "GBP", "UK Pound");
    private final Currency eur = new Currency("57.34", "EUR", "Euro");
    private final Currency yen = new Currency("111.42", "YEN", "Japanese Yen");
    private final Set<Currency> currencies = Sets.newHashSet(usd, gbp, eur, yen);


    private final ObjectMapper objectMapper = new ObjectMapper();


    @Test
    public void testEqualsContract() {
        // Fine to suppress null fields warning since constructor forbids nulls
        EqualsVerifier.forClass(CurrencyDataset.class).suppress(Warning
                .NULL_FIELDS).verify();
    }


    @Test( expected = NullPointerException.class )
    public void testConstructorWithNullCurrencyArray() throws Exception {
        new CurrencyDataset(null, 1);
    }

    @Test( expected = IllegalArgumentException.class )
    public void testConstructorWithNegativeTimestamp() throws Exception {
        new CurrencyDataset(Collections.<Currency>emptySet(), -1);
    }

    @Test
    public void testToObservable() throws Exception {
        Collection<Currency> ret = new CurrencyDataset(currencies, 1L)
                .asObservable()
                .toList()
                .toBlocking().single();

        assertThat(ret).containsExactlyElementsOf(currencies);
    }

    @Test
    public void testJSONDeserialization() throws Exception {
        final String json = "{\"currencies\":[{\"code\":\"GBP\"," +
                "\"value\":\"3.6732\",\"name\":\"UK Pound\"}," +
                "{\"code\":\"USD\",\"value\":\"1\",\"name\":\"US Dollars\"}," +
                "{\"code\":\"EUR\",\"value\":\"57.34\",\"name\":\"Euro\"}," +
                "{\"code\":\"YEN\",\"value\":\"111.42\"," +
                "\"name\":\"Japanese Yen\"}],\"timestamp\":1415210401}";
        final CurrencyDataset expectedCurrencyDataset =
                new CurrencyDataset(currencies, 1415210401L);

        final CurrencyDataset currencyDatasetFromJSON =
                objectMapper.readValue(json, CurrencyDataset.class);

        assertThat(currencyDatasetFromJSON).isEqualTo(expectedCurrencyDataset);
    }

    @Test
    public void testJSONSerializeThenDeserialize() throws Exception {
        final CurrencyDataset expectedCurrencyDataset = new CurrencyDataset(currencies,
                1415210401L);

        ObjectMapper objectMapper = new ObjectMapper();

        final String serializedJSON = objectMapper.writeValueAsString
                (expectedCurrencyDataset);

        final CurrencyDataset currencyDatasetFromJSON = objectMapper.readValue
                (serializedJSON, CurrencyDataset.class);

        assertThat(currencyDatasetFromJSON).isEqualTo(expectedCurrencyDataset);
    }
}
