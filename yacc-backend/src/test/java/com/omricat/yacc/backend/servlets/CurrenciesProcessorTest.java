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

package com.omricat.yacc.backend.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.omricat.yacc.backend.Config;
import com.omricat.yacc.backend.api.CurrencyService;
import com.omricat.yacc.backend.api.NamesService;
import com.omricat.yacc.backend.datastore.DataStore;
import com.omricat.yacc.backend.datastore.NamesStore;
import com.omricat.yacc.backend.utils.MockUtils;
import com.omricat.yacc.data.model.CurrencyCode;

import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Map;

import static org.mockito.Mockito.when;

public class CurrenciesProcessorTest {

    static final Map<String, String> RAW_CURRENCY_DATA =
            ImmutableMap.of("DateTime", "1417390802", "USD",
                    "1\r", "GBP", "1.5\r", "EUR", "0.893\r");

    static final Map<CurrencyCode, String> NAMES_DATA = ImmutableMap.of(
            new CurrencyCode("EUR"), "Euro", new CurrencyCode("GBP"),
            "British Pound Sterling", new CurrencyCode("USD"), "US Dollar");

    private ObjectMapper mapper = new ObjectMapper();

    private CurrenciesProcessor currenciesProcessorUnderTest;

    @Mock
    private CurrencyService mockCurrencyService;

    @Mock
    private NamesService mockNamesService;

    @Mock
    private DataStore currencyStore;

    @Mock
    private DataStore namesStore;

    @Before
    public void setup() throws IOException {

        when(this.mockCurrencyService.getLatestCurrencies()).thenReturn
                (ImmutableList.of(RAW_CURRENCY_DATA));

        when(this.mockNamesService.getCurrencyNames()).thenReturn(NAMES_DATA);

        final CurrencyService mockCurrencyService =
                MockUtils.getMockService(CurrencyService.class,
                        Config.CURRENCY_DATA_ENDPOINT, this.mockCurrencyService);

        final NamesService mockNamesService =
                MockUtils.getMockService(NamesService.class,
                        Config.CURRENCY_NAMES_ENDPOINT, this.mockNamesService);


        currenciesProcessorUnderTest = new CurrenciesProcessor(
                mockCurrencyService, mockNamesService, mapper, currencyStore,
                namesStore);
    }

    @After
    public void tearDown() {
        currenciesProcessorUnderTest = null;
    }


    @Test
    public void test() throws Exception {

    }

    private static class MockNamesStore extends NamesStore {

        private MockNamesStore() {
            super();
        }

        @NotNull public static MockNamesStore getInstance() {
            return new MockNamesStore();
        }

        @NotNull @Override public Reader getReader() {
            return new StringReader("{\"USD\":\"US " +
                    "Dollars\"," +
                    "\"EUR\":\"Euros\",\"GBP\":\"UK Pounds\"}");
        }
    }

}
