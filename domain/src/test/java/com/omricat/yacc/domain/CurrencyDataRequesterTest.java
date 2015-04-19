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

package com.omricat.yacc.domain;

import com.google.common.collect.Sets;
import com.omricat.yacc.data.network.CurrenciesService;
import com.omricat.yacc.data.model.Currency;
import com.omricat.yacc.data.model.CurrencyDataset;
import com.omricat.yacc.data.persistence.Persister;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import rx.Observable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith( MockitoJUnitRunner.class )
public class CurrencyDataRequesterTest {

    final CurrencyDataset currencyDataset = new CurrencyDataset(Sets.newHashSet(
            new Currency("1", "USD", "US Dollars"),
            new Currency("3.6732", "GBP", "UK Pound"),
            new Currency("57.34", "EUR", "Euro"),
            new Currency("111.42", "YEN", "Japanese Yen")), 0);

    @Mock
    Persister<String,CurrencyDataset> persister;

    @Mock
    CurrenciesService networkService;

    @Mock
    IsDataStalePredicate predicate;

    @Before
    public void setUp() throws Exception {

        when(persister.put(anyString(),eq(currencyDataset)))
                .thenReturn(Observable.just(currencyDataset));
    }

    @Test(expected = NullPointerException.class)
    public void testCreation_1stParamNull() throws Exception {
        CurrencyDataRequester.create(null,
                networkService, predicate);
    }

    @Test(expected = NullPointerException.class)
    public void testCreation_2ndParamNull() throws Exception {
        CurrencyDataRequester.create(persister, null, predicate);
    }

    @Test(expected = NullPointerException.class)
    public void testCreation_3rdParamNull() throws Exception {
        CurrencyDataRequester.create(persister, networkService, null);
    }

    @Test
    public void testRequest_LiveDataInPersister() throws Exception {

        when(persister.get(anyString())).thenReturn(Observable.just(currencyDataset));

        when(predicate.call(any(CurrencyDataset.class))).thenReturn(true);

        final CurrencyDataset ret = CurrencyDataRequester
                .create(persister,
                        networkService, predicate)
            .request()
            .toBlocking()
            .single();

        assertThat(ret).isEqualTo(currencyDataset);
    }

    @Test
    public void testRequestCallsNetworkService_StaleDataInPersister() throws
            Exception {

        when(persister.get(anyString()))
                .thenReturn(Observable.just(currencyDataset));

        when(predicate.call(any(CurrencyDataset.class))).thenReturn(false);

        when(networkService.getAllCurrencies())
                .thenReturn(Observable.just(currencyDataset));

        final CurrencyDataset ret = CurrencyDataRequester
                .create(persister,
                        networkService, predicate)
                .request()
                .toBlocking()
                .single();

        verify(networkService).getAllCurrencies();

        assertThat(ret).isEqualTo(currencyDataset);
    }

    @Test
    public void testRequestCallsNetworkService_EmptyPersister() throws
            Exception {
        when(persister.get(anyString()))
                .thenReturn(Observable.<CurrencyDataset>empty());

        when(networkService.getAllCurrencies())
                .thenReturn(Observable.just(currencyDataset));

        final CurrencyDataset ret = CurrencyDataRequester
                .create(persister,
                        networkService, IsDataStalePredicate.createDefault())
                .request()
                .toBlocking()
                .single();

        verify(networkService).getAllCurrencies();

        assertThat(ret).isEqualTo(currencyDataset);

    }

    @Test
    public void testRequestNetworkServiceCallsPutOnPersister() throws Exception {
        when(persister.get(anyString()))
                .thenReturn(Observable.<CurrencyDataset>empty());

        when(networkService.getAllCurrencies())
                .thenReturn(Observable.just(currencyDataset));

        final CurrencyDataset ret = CurrencyDataRequester
                .create(persister,
                        networkService, IsDataStalePredicate.createDefault())
                .request()
                .toBlocking()
                .single();

        verify(persister)
                .put(CurrencyDataRequester.PERSISTENCE_KEY, currencyDataset);
    }
}
