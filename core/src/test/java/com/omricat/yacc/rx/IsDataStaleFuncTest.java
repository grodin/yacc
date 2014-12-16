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

package com.omricat.yacc.rx;

import com.google.common.collect.Sets;
import com.omricat.yacc.model.Currency;
import com.omricat.yacc.model.CurrencySet;

import org.junit.Test;

import java.util.HashSet;

import rx.Observable;
import rx.functions.Func0;

import static org.assertj.core.api.Assertions.assertThat;

public class IsDataStaleFuncTest {

    private final Currency usd = new Currency("1.0",
            "USD", "");
    private final HashSet<Currency> currencies = Sets.newHashSet(usd);
    private final CurrencySet currencySet = new CurrencySet(currencies, 0);

    @Test(expected = NullPointerException.class)
    public void testCreateWithNull() throws Exception {
        IsDataStaleFunc.create(null,IsDataStaleFunc.CURRENT_EPOCH_FUNC);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCallWithNegativeEpoch() throws Exception {
        IsDataStaleFunc.create(Observable.<CurrencySet>empty(),new Func0<Long>() {


            @Override public Long call() {
                return (long) -1;
            }
        }).call(CurrencySet.EMPTY);
    }

    @Test
    public void testCallWithLiveData() throws Exception {
        CurrencySet ret = IsDataStaleFunc.create(Observable.just(CurrencySet
                        .EMPTY),
                new Func0<Long>() {
                    @Override public Long call() {
                        return (long) 2 * 60; // two minutes after 0 epoch
                    }
                }).call(currencySet).toBlocking().single();

        assertThat(ret.getCurrencies()).contains(usd);
    }

    @Test
    public void testCallWithStaleData() throws Exception {
        CurrencySet ret = IsDataStaleFunc.create(Observable.just(CurrencySet.EMPTY),
                new Func0<Long>() {
                    @Override public Long call() {
                        return (long) (365 * 24 * 60 * 60); // one year after
                        // zero epoch
                    }
                }).call(currencySet).toBlocking().single();

        assertThat(ret.getCurrencies()).doesNotContain(usd);

    }
}
