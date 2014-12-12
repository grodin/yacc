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
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.development.testing
        .LocalBlobstoreServiceTestConfig;
import com.google.appengine.tools.development.testing
        .LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing
        .LocalFileServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;
import com.omricat.yacc.backend.api.CurrencyService;
import com.omricat.yacc.data.CurrencySet;
import com.omricat.yacc.data.Currency;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import retrofit.MockRestAdapter;
import retrofit.RestAdapter;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class CurrenciesProcessorTest {

    private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
            new LocalTaskQueueTestConfig(), new LocalFileServiceTestConfig(),
            new LocalBlobstoreServiceTestConfig(),
            new LocalDatastoreServiceTestConfig());

    private ObjectMapper mapper = new ObjectMapper();
    private GcsFilename gcsFilename = new GcsFilename(Config.BUCKET,
            Config.LATEST_CURRENCY_FILENAME);

    private final Logger log = Logger.getLogger
            (DownloadLatestCurrenciesServlet.class
                    .getName());

    private CurrenciesProcessor currenciesProcessorUnderTest;
    private MockRestAdapter mockRestAdapter;

    @Before
    public void setup() throws IOException {
        helper.setUp();

        final GcsService gcsService = GcsServiceFactory.createGcsService();

        final RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint
                (Config.CURRENCY_DATA_ENDPOINT).build();
        mockRestAdapter = MockRestAdapter.from(restAdapter);
        final CurrencyService mockCurrencyService = mockRestAdapter.create
                (CurrencyService.class,
                        new MockCurrencyService());

        currenciesProcessorUnderTest = new CurrenciesProcessor(
                mockCurrencyService, mapper);
    }

    @After
    public void tearDown() {
        helper.tearDown();

        currenciesProcessorUnderTest = null;
    }

    @Test
    public void testTimestamp() {
        CurrencySet currs = null;
        try {
            currs = currenciesProcessorUnderTest.download();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertEquals(currs.getLastUpdatedTimestamp(), 1417390802);
    }

    @Test
    public void testCurrencyData() {
        CurrencySet currs = null;
        try {
            currs = currenciesProcessorUnderTest.download();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Set<Currency> currSet = new HashSet<>();
        currSet.add(new Currency("1", "USD", null));
        currSet.add(new Currency("1.5", "GBP", null));
        assertTrue(currs.getCurrencies().containsAll(currSet));
    }


    private static class MockCurrencyService implements CurrencyService {

        private final Map<String, String> currencyMap;

        MockCurrencyService() {
            currencyMap = new HashMap<>();
            currencyMap.put("DateTime", "1417390802");
            currencyMap.put("USD", "1\r");
            currencyMap.put("GBP", "1.5\r");
            currencyMap.put("EUR", "0.893\r");

        }


        @Override
        public List<Map<String, String>> getLatestCurrencies() {
            final ArrayList<Map<String, String>> list = new ArrayList<>();
            list.add(0, currencyMap);
            return list;
        }
    }

}
