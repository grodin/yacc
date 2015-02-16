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

package com.omricat.yacc.backend.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.labs.repackaged.com.google.common.collect.ImmutableMap;
import com.omricat.yacc.backend.api.NamesService;
import com.omricat.yacc.backend.datastore.DataStore;
import com.omricat.yacc.data.model.CurrencyCode;

import org.junit.Test;
import org.mockito.Mockito;

import java.io.StringWriter;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NamesHelperTest {

    public static final CurrencyCode EUR = new CurrencyCode("EUR");
    public static final String EURO = "Euro";
    public static final CurrencyCode GBP = new CurrencyCode("GBP");
    public static final String STERLING = "British Pound Sterling";
    public static final CurrencyCode USD = new CurrencyCode("USD");
    public static final String US_DOLLAR = "US Dollar";
    static final Map<CurrencyCode, String> NAMES_DATA = ImmutableMap.of(
            EUR, EURO, GBP, STERLING, USD, US_DOLLAR);


    private final NamesService namesService = Mockito.mock(NamesService.class);
    private final ObjectMapper mapper = new ObjectMapper();
    private final String NOT_IMPLEMENTED = "Test not implemented";
    private final DataStore store = Mockito.mock(DataStore.class);

    @Test( expected = NullPointerException.class )
    public void testGetInstance_Null1stParam() throws Exception {
        NamesHelper.getInstance(null, namesService);
    }

    @Test( expected = NullPointerException.class )
    public void testGetInstance_Null2ndParam() throws Exception {
        NamesHelper.getInstance(mapper, null);
    }

    @Test
    public void testGetAndStoreCurrencyNames() throws Exception {
        when(namesService.getCurrencyNames()).thenReturn(NAMES_DATA);

        final StringWriter stringWriter = new StringWriter();
        when(store.getWriter()).thenReturn(stringWriter);

        Map<CurrencyCode,String> ret = NamesHelper.getInstance(mapper,namesService)
                .getAndStoreCurrencyNames(store);

        verify(namesService).getCurrencyNames();

        assertThat(ret).hasSameSizeAs(NAMES_DATA).containsEntry(GBP,STERLING)
                .containsEntry(EUR,EURO).containsEntry(USD,US_DOLLAR);

        assertThat(stringWriter.toString()).isEqualTo("{\"CurrencyKey{key" +
                "='EUR'}\":\"Euro\",\"CurrencyKey{key='GBP'}\":\"British " +
                "Pound Sterling\",\"CurrencyKey{key='USD'}\":\"US Dollar\"}");
    }

    @Test
    public void testGet_ServiceTimeout() throws Exception {
        // TODO: Write names service timeout test in NamesHelperTest

        fail(NOT_IMPLEMENTED);
    }

    @Test
    public void testGet_404() throws Exception {
        // TODO: Write names service 404 test in NamesHelperTest

        fail(NOT_IMPLEMENTED);
    }
}
