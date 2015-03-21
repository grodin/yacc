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

import com.google.common.collect.Lists;
import com.omricat.yacc.data.model.Currency;
import com.omricat.yacc.data.model.CurrencyCode;
import com.omricat.yacc.data.persistence.Persister;
import com.omricat.yacc.data.TestPersister;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import rx.Observable;

import static com.omricat.yacc.data.TestCurrencies.*;
import static com.omricat.yacc.data.TestCurrencyCodes.*;
import static com.omricat.yacc.domain.SourceCurrencyProvider.PopularCurrencies;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith( MockitoJUnitRunner.class )
public class SourceCurrencyProviderTest {

    Observable<? extends Collection<Currency>> selectedCurrencies;


    @Mock
    Persister<String, CurrencyCode> persister;


    @Test( expected = NullPointerException.class )
    public void testConstructor_1stParamNull() throws Exception {
        new SourceCurrencyProvider(null, selectedCurrencies);
    }

    @Test( expected = NullPointerException.class )
    public void testConstructor_2ndParamNull() throws Exception {
        new SourceCurrencyProvider(persister, null);
    }


    @Test( expected = NullPointerException.class )
    public void testPersist_NullParam() throws Exception {
        new SourceCurrencyProvider(persister, selectedCurrencies).persist(null);
    }

    @Test
    public void testPersistDelegatesToPersister() throws Exception {
        selectedCurrencies = Observable.empty();

        TestPersister<CurrencyCode> testPersister = new TestPersister<>();

        Currency ret = new SourceCurrencyProvider(testPersister, selectedCurrencies)
                .persist(USD)
                .toBlocking().single();

        assertThat(testPersister.internalMap()).containsValue(USD_CODE);
    }

    @Test
    public void testGetLatestSourceCurrency_NoSelectedCurrencies() throws
            Exception {

        selectedCurrencies = Observable.just(Collections.<Currency>emptySet());

        when(persister.get(anyString())).thenReturn(Observable.just(USD_CODE));

        List<?> ret = new SourceCurrencyProvider(persister, selectedCurrencies)
                .getLatestSourceCurrency()
                .toList().toBlocking().single();

        assertThat(ret).isEmpty();
    }

    @Test
    public void
    latestSourceCurrComesFromPopularsInOrderWhenPossible() throws Exception {

        selectedCurrencies = Observable.just(
                Lists.newArrayList(CHF, GBP, XCD, RUB));

        when(persister.get(anyString())).thenReturn(Observable
                .<CurrencyCode>empty());

        CurrencyCode ret = new SourceCurrencyProvider(persister, selectedCurrencies)
                .getLatestSourceCurrency()
                .toBlocking().single().getCode();

        assertThat(ret).isIn(PopularCurrencies.asList()).isEqualTo(GBP_CODE);
    }

    @Test
    public void
    latestSourceCurrIsFirstAlphabeticallyIfNotInPopularList_EmptyPersister()
            throws Exception {

        selectedCurrencies = Observable.just(Lists.newArrayList(AMD, XCD, RUB));

        when(persister.get(anyString()))
                .thenReturn(Observable.just(USD_CODE));

        CurrencyCode ret = new SourceCurrencyProvider(persister, selectedCurrencies)
                .getLatestSourceCurrency()
                .toBlocking().single().getCode();

        assertThat(ret).isNotIn(PopularCurrencies.asList()).isEqualTo(AMD_CODE);

    }

    @Test
    public void testPersisterReturnsMoreThanOneCode() throws Exception {
        selectedCurrencies = Observable.just(Lists.newArrayList(USD, GBP, EUR));

        when(persister.get(anyString()))
                .thenReturn(Observable.just(USD_CODE, GBP_CODE));

        CurrencyCode ret = new SourceCurrencyProvider(persister, selectedCurrencies)
                .getLatestSourceCurrency()
                .toBlocking().single().getCode();

        assertThat(ret).isNotIn(PopularCurrencies.asList()).isEqualTo(AMD_CODE);


    }

    @Test
    public void testGetLatestSourceCurrency() throws Exception {
        selectedCurrencies = Observable.just(
                Lists.newArrayList(CHF, GBP, XCD, RUB));
        when(persister.get(anyString())).thenReturn(Observable.just(GBP_CODE));

        Currency ret = new SourceCurrencyProvider(persister, selectedCurrencies)
                .getLatestSourceCurrency()
                .toBlocking().single();

        assertThat(ret).isEqualTo(GBP);
    }

    @Test
    public void testName() throws Exception {

    }
}
