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

import org.junit.Before;
import org.junit.Test;

import static com.omricat.yacc.data.TestCurrencies.*;
import static com.omricat.yacc.domain.SourceCurrencyProvider.PopularCurrencies.CurrencyByCodePopularsFirstComparator;
import static org.assertj.core.api.Assertions.assertThat;

public class CurrencyComparatorTest {

    CurrencyByCodePopularsFirstComparator comparator;

    @Before
    public void setUp() throws Exception {
        comparator = new CurrencyByCodePopularsFirstComparator();

    }

    @Test(expected = NullPointerException.class)
    public void testCompare_Null1stParam() throws Exception {
        comparator.compare(null, USD);
    }

    @Test(expected = NullPointerException.class)
    public void testCompare_Null2ndParam() throws Exception {
        comparator.compare(GBP, null);
    }

    @Test
    public void testEqualCodes_BothInPopulars() throws Exception {

        final int ret = comparator.compare(USD, USD);
        assertThat(ret).isEqualTo(0);

    }

    @Test
    public void testEqualCodes_NeitherInPopulars() throws Exception {

        final int ret = comparator.compare(AUD, AUD);
        assertThat(ret).isEqualTo(0);
    }

    @Test
    public void testDistinctCodes_BothInPopulars() throws Exception {

        final int ret = comparator.compare(USD, GBP);
        final int rev = comparator.compare(GBP, USD);
        assertThat(ret).isLessThan(0).isEqualTo(-1 * rev);
    }

    @Test
    public void testDistinctCodes_FirstInPopulars() throws Exception {

        final int ret = comparator.compare(USD, AUD);
        final int rev = comparator.compare(AUD, USD);
        assertThat(ret).isLessThan(0).isEqualTo(-1 * rev);
    }

    @Test
    public void testDistinctCodes_NeitherInPopulars() throws Exception {

        final int ret = comparator.compare(AUD, BTN);
        final int rev = comparator.compare(BTN, AUD);
        assertThat(ret).isLessThan(0).isEqualTo(-1 * rev);
    }


}
