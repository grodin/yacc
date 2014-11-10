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

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class CurrenciesTest {

    @Test
    public void testEqualsContract() {
        // Fine to suppress null fields warning since constructor forbids nulls
        EqualsVerifier.forClass(Currencies.class).suppress(Warning
                .NULL_FIELDS).verify();
    }


    @Test
    public void testJsonDeserialisation() {
        final Currency[] currencies = {
                new Currency("1", "USD")
                , new Currency("3.6732", "AED")
                , new Currency("57.34", "AFN")
                , new Currency("111.42", "ALB")
        };
        final String json = "{\"timestamp\":\"1415210401\"," +
                "\"currencies\":[{\"value\":\"1\",\"code\":\"USD\"}," +
                "{\"value\":\"3.6732\",\"code\":\"AED\"}," +
                "{\"value\":\"57.34\",\"code\":\"AFN\"}," +
                "{\"value\":\"111.42\",\"code\":\"ALB\"}]}";
        final Currencies currs1 = new Currencies(currencies, 1415210401L);
        ObjectMapper objectMapper = new ObjectMapper();
        Currencies currs2 = null;
        try {
            currs2 = objectMapper.readValue(json, Currencies
                    .class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Assert.assertTrue(currs1.equals(currs2));
    }
}
