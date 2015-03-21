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

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.omricat.yacc.data.model.Currency;
import com.omricat.yacc.data.model.CurrencyCode;
import com.omricat.yacc.data.model.CurrencyDataset;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import rx.Observable;

import static com.omricat.yacc.data.TestCurrencies.*;
import static com.omricat.yacc.data.TestCurrencyCodes.GBP_CODE;
import static com.omricat.yacc.data.TestCurrencyCodes.USD_CODE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith( MockitoJUnitRunner.class )
public class SelectedCurrenciesProviderTest {

    private final CurrencyDataset currencyDataset =
            new CurrencyDataset(Sets.newHashSet(USD, GBP, EUR), 10L);

    Observable<Set<CurrencyCode>> currencyCodes =
            Observable.<Set<CurrencyCode>>just(ImmutableSet.of(USD_CODE, GBP_CODE));

    @Mock
    CurrencyDataRequester requester;

    @Test(expected = NullPointerException.class)
    public void testConstructor_1stParamNull() throws Exception {
        new SelectedCurrenciesProvider(null, requester);
    }

    @Test(expected = NullPointerException.class)
    public void testConstructor_2ndParamNull() throws Exception {
        new SelectedCurrenciesProvider(currencyCodes, null);
    }

    @Test
    public void testGetCurrencies() throws Exception {
        when(requester.request()).thenReturn(Observable.just(currencyDataset));

        SelectedCurrenciesProvider selectedCurrenciesProvider = new SelectedCurrenciesProvider
                (currencyCodes, requester);

        Collection<Currency> ret = selectedCurrenciesProvider.getCurrencyData()
                .toBlocking().single();

        verify(requester).request();

        assertThat(ret).contains(USD,GBP).doesNotContain(EUR);
    }

    @Test
    public void testGetCurrenciesReturnsEmptyObservable_NoSelectedCurrencies
            () throws Exception {
        when(requester.request()).thenReturn(Observable.just(currencyDataset));

        SelectedCurrenciesProvider selectedCurrenciesProvider =
                new SelectedCurrenciesProvider
                (Observable.just(Collections.<CurrencyCode>emptySet()), requester);

        Collection<?> ret = selectedCurrenciesProvider.getCurrencyData()
                .toList()
                .toBlocking().single();

        assertThat(ret).isEmpty();
    }
}
