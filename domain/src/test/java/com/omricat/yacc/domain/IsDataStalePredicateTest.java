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
import com.omricat.yacc.data.model.Currency;
import com.omricat.yacc.data.model.CurrencyDataset;

import org.junit.Test;

import java.util.HashSet;

import rx.functions.Func0;

import static org.assertj.core.api.Assertions.assertThat;

public class IsDataStalePredicateTest {

    private final Currency usd = new Currency("1.0",
            "USD", "");
    private final HashSet<Currency> currencies = Sets.newHashSet(usd);
    private final CurrencyDataset currencyDataset = new CurrencyDataset(currencies, 0);

    @Test(expected = NullPointerException.class)
    public void testConstructWithNullFirstParam() throws Exception {
        com.omricat.yacc.data.persistence.IsDataStalePredicate.create(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructWithNonPositiveStaleInterval() throws Exception {
        com.omricat.yacc.data.persistence.IsDataStalePredicate.create(com
                .omricat.yacc.data.persistence.IsDataStalePredicate
                .CURRENT_EPOCH_FUNC, -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCallWithInvalidEpochFunc() throws Exception {
        com.omricat.yacc.data.persistence.IsDataStalePredicate.create(new Func0<Long>() {
            @Override public Long call() {
                return -1L;
            }
        });

    }

    @Test
    public void testStaleData() throws Exception {
        boolean ret = com.omricat.yacc.data.persistence.IsDataStalePredicate
                .create(new Func0<Long>() {
                    @Override public Long call() {
                        return 10L * 24L * 60L * 60L; // 10 days
                    }
                }).call(currencyDataset);

        assertThat(ret).isTrue();
    }

    @Test
    public void testLiveData() throws Exception {
        boolean ret = IsDataStalePredicate.create(new Func0<Long>() {
            @Override public Long call() {
                return 0L; // start of epoch
            }
        }).call(currencyDataset);

        assertThat(ret).isFalse();

    }
}
