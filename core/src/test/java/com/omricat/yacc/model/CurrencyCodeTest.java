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

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import static org.assertj.core.api.Assertions.assertThat;

public class CurrencyCodeTest {

    @Test(expected = NullPointerException.class)
    public void testConstruction_Null() throws Exception {
        new CurrencyCode(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstruction_IncorrectChars() throws Exception {
        new CurrencyCode("gbp");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstruction_TooShortString() throws Exception {
        new CurrencyCode("GB");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstruction_TooLongString() throws Exception {
        new CurrencyCode("GBOD");
    }

    @Test
    public void testConstruction() throws Exception {
        String ret = new CurrencyCode("USD").toString();
        assertThat(ret).isEqualTo("USD");
    }

    @Test
    public void testEqualsContract() throws Exception {
        EqualsVerifier.forClass(CurrencyCode.class)
                .usingGetClass()
                .suppress(Warning.NULL_FIELDS);
    }

    @Test
    public void testJsonSerialization() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();

        final String json = mapper.writeValueAsString(new CurrencyCode("EUR"));

        assertThat(json).isEqualTo("\"EUR\"");
    }

    @Test
    public void testJsonDeserialization_ValidData() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();

        final CurrencyCode key = mapper.readValue("\"EUR\"",CurrencyCode.class);

        assertThat(key).isEqualTo(new CurrencyCode("EUR"));

    }

    @Test(expected = JsonMappingException.class)
    public void testJsonDeserialization_InvalidData() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();

        final CurrencyCode key = mapper.readValue("\"er\"", CurrencyCode.class);
    }
}
