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

package com.omricat.yacc.rx.persistence;

import com.google.common.collect.Sets;
import com.omricat.yacc.model.Currency;
import com.omricat.yacc.model.CurrencyDataset;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashSet;

import rx.Observable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class IsDataStaleFuncTest {

    private final Currency usd = new Currency("1.0",
            "USD", "");
    private final HashSet<Currency> currencies = Sets.newHashSet(usd);
    private final CurrencyDataset currencyDataset = new CurrencyDataset(currencies, 0);

    @Test(expected = NullPointerException.class)
    public void testCreateWithNull() throws Exception {
        IsDataStaleFunc.create(null);
    }

    @Test
    public void testCallWithLiveData() throws Exception {
        final IsDataStalePredicate mockPredicate = Mockito.mock
                (IsDataStalePredicate.class);

        when(mockPredicate.call(any(CurrencyDataset.class))).thenReturn(false);

        final IsDataStaleFunc dataStaleFunc = new IsDataStaleFunc
                (Observable.just(CurrencyDataset.EMPTY), mockPredicate);

        CurrencyDataset ret = dataStaleFunc.call(currencyDataset)
                .toBlocking()
                .single();

        assertThat(ret.getCurrencies()).contains(usd);
    }

    @Test
    public void testCallWithStaleData() throws Exception {
        final IsDataStalePredicate mockPredicate = Mockito.mock
                (IsDataStalePredicate.class);

        when(mockPredicate.call(any(CurrencyDataset.class))).thenReturn(true);

        final IsDataStaleFunc dataStaleFunc = new IsDataStaleFunc
                (Observable.just(CurrencyDataset.EMPTY), mockPredicate);

        CurrencyDataset ret = dataStaleFunc.call(currencyDataset)
                .toBlocking()
                .single();

        assertThat(ret.getCurrencies()).doesNotContain(usd);

    }
}
