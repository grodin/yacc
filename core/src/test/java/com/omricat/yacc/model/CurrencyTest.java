package com.omricat.yacc.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import static org.assertj.core.api.Assertions.assertThat;

public class CurrencyTest {

    private final Currency CUR = new Currency("1", "CUR", "", "");
    private final int DIVISION_SCALE = 8;

    @Test( expected = NullPointerException.class )
    public void testConvert_1stParamNull() throws Exception {
        Currency.convert(null, CUR, new BigDecimal(1));
    }

    @Test( expected = NullPointerException.class )
    public void testConvert_2ndParamNull() throws Exception {
        Currency.convert(CUR, null, new BigDecimal(1));
    }

    @Test( expected = NullPointerException.class )
    public void testConvert_3rdParamNull() throws Exception {
        Currency.convert(CUR, CUR, null);
    }

    @Test( expected = IllegalArgumentException.class )
    public void testConvert_NegativeValue() throws Exception {
        Currency.convert(CUR, CUR, new BigDecimal(-1));
    }

    @Test
    public void testConvert() {
        Currency source = new Currency("7.00", "CUR", "", "");
        Currency target = new Currency("17", "CUS", "", "");

        final BigDecimal TEN = new BigDecimal(10);
        final BigDecimal SEVEN = new BigDecimal(7);
        final BigDecimal SEVENTEEN = new BigDecimal(17);

        BigDecimal val = Currency.convert(source, target, TEN);

        assertThat(val).isEqualByComparingTo(TEN.multiply(
                SEVEN.divide(SEVENTEEN, DIVISION_SCALE,
                        BigDecimal.ROUND_HALF_EVEN)));
    }

    @Test( expected = NullPointerException.class )
    public void testConversionRatio_1stParamNull() throws Exception {
        Currency.conversionRatio(null, CUR);
    }

    @Test( expected = NullPointerException.class )
    public void testConversionRatio_2ndParamNull() throws Exception {
        Currency.conversionRatio(CUR, null);
    }

    @Test
    public void testConversionRatio() throws Exception {
        Currency curr1 = new Currency("4", "CUR", "", "");
        Currency curr2 = new Currency("3", "CUS", "", "");

        BigDecimal ret = Currency.conversionRatio(curr1, curr2);

        assertThat(ret).isEqualByComparingTo(new BigDecimal(4).divide(new
                BigDecimal(3), DIVISION_SCALE, RoundingMode.HALF_EVEN));
    }

    @Test(expected = NullPointerException.class)
    public void testConstructor_1stParamNull() throws Exception {
        new Currency(null, "EUR", "Euro");
    }

    @Test(expected = NullPointerException.class)
    public void testConstructor_2ndParamNull() throws Exception {
        new Currency("1.0", null, "Euro");
    }

    @Test(expected = NullPointerException.class)
    public void testConstructor_3rdParamNull() throws Exception {
        new Currency("1.0", "EUR", null);
    }

    @Test( expected = NullPointerException.class )
    public void testConstructor_4thParamNull() throws Exception {
        new Currency("1.0", "EUR", "Euro", null);
    }

    @Test
    public void testConstructFromString() {
        Currency curr = new Currency("13.3", "CUR", "Name of currency",
                "Description of currency");

        assertThat(curr.getRateInUSD()).isEqualByComparingTo("13.3");

        assertThat(curr.getCode()).isEqualTo(new CurrencyCode("CUR"));

        assertThat(curr.getName()).isNotEmpty().isEqualTo("Name of currency");

        assertThat(curr.getDescription()).isNotEmpty()
                .isEqualTo("Description of currency");
    }

    @Test
    public void testEqualsContract() {
        // Fine to suppress null fields warning since constructor forbids nulls
        EqualsVerifier.forClass(Currency.class).suppress(Warning.NULL_FIELDS)
                .verify();
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


}
