package com.omricat.yacc.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import static junit.framework.TestCase.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test of Currency class.
 * <p/>
 * Created by jsc on 02/11/14.
 */
public class CurrencyTest {

    @Test
    public void testConvert() {
        Currency curr1 = new Currency("1.0", "CUR", "", "");
        Currency curr2 = new Currency("2.0", "CUS", "", "");
        BigDecimal val = Currency.convert(curr1, curr2, new BigDecimal(10));
        assertTrue(val.compareTo(new BigDecimal(5)) == 0);
    }

    @Test
    public void testConstructFromString() {
        Currency curr = new Currency("13.3", "CUR", "", "");
        assertTrue("Value in USD is " + curr.getValueInUSD(),
                curr.getValueInUSD().compareTo
                        (new BigDecimal("13.3")) == 0);
    }

    @Test
    public void testEqualsContract() {
        // Fine to suppress null fields warning since constructor forbids nulls
        EqualsVerifier.forClass(Currency.class).suppress(Warning.NULL_FIELDS)
                .verify();
    }


    @Test
    public void testJsonDeserialisation() throws IOException {
        String json = "{\"value\":\"3.6732\",\"code\":\"EUR\"," +
                "\"name\":\"Euro\"}";
        ObjectMapper objMapper = new ObjectMapper();
        Currency currFromJSON = null;
        currFromJSON = objMapper.readValue(json, Currency.class);
        Currency curr = new Currency("3.6732", "EUR", "Euro");
        assertThat(curr).isEqualTo(currFromJSON);
    }

    @Test
    public void testJsonSerialisation() throws JsonProcessingException {
        Currency curr = new Currency("3.6732", "EUR", "Euro");
        ObjectMapper objectMapper = new ObjectMapper();
        String convertedJson = objectMapper.writeValueAsString(curr);
        final String expected = "{\"code\":\"EUR\",\"value\":\"3.6732\"," +
                "\"name\":\"Euro\"}";
        assertThat(convertedJson).isEqualTo(expected);
    }

    @Test(expected = NullPointerException.class)
    public void testConstructor1stParamNull() throws Exception {
        new Currency(null,"EUR","Euro");
    }

    @Test(expected = NullPointerException.class)
    public void testConstructor2ndParamNull() throws Exception {
        new Currency("1.0",null,"Euro");
    }

    @Test(expected = NullPointerException.class)
    public void testConstructor3rdParamNull() throws Exception {
        new Currency("1.0","EUR",null);
    }

    @Test(expected = NullPointerException.class)
    public void testConstructor4thParamNull() throws Exception {
        new Currency("1.0","EUR","Euro",null);
    }



}
