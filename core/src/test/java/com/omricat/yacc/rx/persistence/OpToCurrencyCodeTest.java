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

package com.omricat.yacc.rx.persistence;

import com.omricat.yacc.model.CurrencyCode;
import com.omricat.yacc.rx.RxSet;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith( MockitoJUnitRunner.class)
public class OpToCurrencyCodeTest {

    @Mock
    RxSet<CurrencyCode> rxSet;

    final CurrencyCode usd = new CurrencyCode("USD");

    @Test(expected = NullPointerException.class)
    public void testConstructor_NullParam() throws Exception {
        new OpToCurrencyCode(null);
    }

    @Test
    public void testCall_AddOp() throws Exception {
        new OpToCurrencyCode(rxSet).call(Operation.add(usd));

        verify(rxSet).add(usd);

    }

    @Test
    public void testCall_RemoveOp() throws Exception {
        new OpToCurrencyCode(rxSet).call(Operation.remove(usd));

        verify(rxSet).remove(usd);
    }
}
