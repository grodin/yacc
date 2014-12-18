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

import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import com.omricat.yacc.debug.TestPersister;
import com.omricat.yacc.model.CurrencyKey;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import rx.Observable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith( MockitoJUnitRunner.class )
public class CurrencyKeyRequesterTest {

    static final Set<CurrencyKey> EMPTY_KEY_SET = Collections.emptySet();

    @Mock
    Persister<String, Set<CurrencyKey>> mockDiskPersister;
    private final CurrencyKey usd = new CurrencyKey("USD");
    private final CurrencyKey gbp = new CurrencyKey("GBP");
    private final CurrencyKey eur = new CurrencyKey("EUR");
    private final CurrencyKey jpy = new CurrencyKey("JPY");
    private final Set<CurrencyKey> keys = Sets.newHashSet(usd, eur, gbp);
    private final String persistenceKey = CurrencyKeyRequester.PERSISTENCE_KEY;

    @Test( expected = NullPointerException.class )
    public void testCreateWithNull() throws Exception {
        CurrencyKeyRequester.create(null);
    }

    @Test
    public void testJustCreatedIsEmpty() throws Exception {
        when(mockDiskPersister.get(CurrencyKeyRequester.PERSISTENCE_KEY))
                .thenReturn(Observable.just(Optional.of(EMPTY_KEY_SET)));

        Set<?> set = CurrencyKeyRequester.create(mockDiskPersister).get()
                .toBlocking().single();

        assertThat(set).isEmpty();
    }

    @Test
    public void testGetWithEmptyPersister() throws Exception {
        when(mockDiskPersister.get(persistenceKey))
                .thenReturn(Observable.just(
                        Optional.<Set<CurrencyKey>>absent()
                ));

        CurrencyKeyRequester.create(mockDiskPersister).get()
                .toBlocking().single();

        verify(mockDiskPersister).get(CurrencyKeyRequester.PERSISTENCE_KEY);
    }

    @Test
    public void testAdd() throws Exception {
        final Set<CurrencyKey> keySet = new HashSet<>();
        keySet.add(usd);

        when(mockDiskPersister.put(anyString(),
                Matchers.<Set<CurrencyKey>>any()))
                .thenReturn(Observable.just(keySet));

        Set<?> set = CurrencyKeyRequester.create(mockDiskPersister).add(usd)
                .toBlocking().single();

        verify(mockDiskPersister).put(persistenceKey, keySet);

        assertThat(set).containsExactly(usd);
    }

    @Test
    public void testAddAll() throws Exception {

        final TestPersister<Set<CurrencyKey>> testPersister = new
                TestPersister<>();
        final CurrencyKeyRequester currencyKeyRequester =
                CurrencyKeyRequester.create(testPersister);
        currencyKeyRequester.add(jpy);
        Set<CurrencyKey> set = currencyKeyRequester.addAll(keys)
                .toBlocking().single();

        assertThat(set).containsAll(keys).contains(jpy);

        Optional<Set<CurrencyKey>> setOptional;
        assertThat((setOptional=testPersister.get(persistenceKey)
                .toBlocking().single()) //unwrap Observable
                .isPresent());

        assertThat(setOptional.get())
                .containsAll(keys)
                .contains(jpy);
    }

    @Test
    public void testRemove() throws Exception {
        // TODO: implement CurrencyKeyRequesterTest#testRemove()
        fail("Test not implemented");
    }

    @Test
    public void testRemoveAll() throws Exception {
        // TODO: implement CurrencyKeyRequesterTest#testRemoveAll()
        fail("Test not implemented");
    }
}
