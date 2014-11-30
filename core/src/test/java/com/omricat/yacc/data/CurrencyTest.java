package com.omricat.yacc.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertTrue;

/**
 * Test of Currency class.
 * <p/>
 * Created by jsc on 02/11/14.
 */
public class CurrencyTest {

    @Test
    public void testConvert() {
        Currency curr1 = new Currency("1.0", "CU1", "", "");
        Currency curr2 = new Currency("2.0", "CU2", "", "");
        BigDecimal val = Currency.convert(curr1, curr2, new BigDecimal(10));
        assertTrue(val.compareTo(new BigDecimal(5)) == 0);
    }

    @Test
    public void testConstructFromString() {
        Currency curr = new Currency("13.3", "", "", "");
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
        String json = "{\"value\":\"3.6732\",\"code\":\"AED\"}";
        ObjectMapper objMapper = new ObjectMapper();
        Currency curr1 = null;
        curr1 = objMapper.readValue(json, Currency.class);
        Currency curr2 = new Currency("3.6732", "AED");
        assertTrue(curr2.equals(curr1));
    }

    @Test
    public void testJsonSerialisation() throws JsonProcessingException {
        Currency curr = new Currency("3.6732", "AED");
        ObjectMapper objectMapper = new ObjectMapper();
        String convertedJson = objectMapper.writeValueAsString(curr);
        final String expected = "{\"value\":\"3.6732\",\"code\":\"AED\"}";
        assertEquals(expected,convertedJson);
    }
}
