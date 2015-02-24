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

import com.omricat.yacc.common.rx.RxSet;
import com.omricat.yacc.data.model.Currency;
import com.omricat.yacc.data.model.CurrencyCode;
import com.omricat.yacc.data.persistence.Persister;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import rx.Observable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith( MockitoJUnitRunner.class )
public class SourceCurrencyTest {

    @Mock
    RxSet<CurrencyCode> codeRxSet;

    @Mock
    Persister<String, Currency> persister;

    private CurrencyCode usdCode = new CurrencyCode("USD");
    private Currency usd = new Currency("1.0", "USD", "US Dollar");

    @Test( expected = NullPointerException.class )
    public void testConstructor_1stParamNull() throws Exception {
        new SourceCurrency(null, codeRxSet);
    }

    @Test( expected = NullPointerException.class )
    public void testConstructor_2ndParamNull() throws Exception {
        new SourceCurrency(persister, null);
    }


    @Test( expected = NullPointerException.class )
    public void testPersist_NullParam() throws Exception {
        new SourceCurrency(persister, codeRxSet).persist(null);
    }

    @Test
    public void testPersistDelegatesToPersister() throws Exception {
        new SourceCurrency(persister, codeRxSet)
                .persist(new Currency("1.0", "USD", "US Dollar"));

        verify(persister).put(Matchers.eq(SourceCurrency
                .PERSISTENCE_KEY), any(Currency.class));
    }

    @Test
    public void testGetLatestSourceCurrency_NoSelectedCurrencies() throws
            Exception {

        when(codeRxSet.asObservable())
            .thenReturn(Observable.<CurrencyCode>empty());

        when(persister.get(anyString())).thenReturn(Observable.just(usd));

        List<?> ret = new SourceCurrency(persister, codeRxSet)
                .getLatestSourceCurrency()
                .toList().toBlocking().single();

        assertThat(ret).isEmpty();
    }

    @Test
    public void testGetLatestSourceCurrency_EmptyPersister() throws Exception {

        when(codeRxSet.asObservable())
                .thenReturn(Observable.just(usdCode));

        when(persister.get(anyString())).thenReturn(Observable.<Currency>empty());

        List<?> ret = new SourceCurrency(persister, codeRxSet)
                .getLatestSourceCurrency()
                .toList().toBlocking().single();

        assertThat(ret).isEmpty();
    }

    @Test
    public void testGetLatestSourceCurrency() throws Exception {
        when(codeRxSet.asObservable())
                .thenReturn(Observable.just(usdCode));
        when(persister.get(anyString())).thenReturn(Observable.just(usd));

        Currency ret = new SourceCurrency(persister, codeRxSet)
                .getLatestSourceCurrency()
                .toBlocking().single();

        assertThat(ret).isEqualTo(usd);
    }
}
