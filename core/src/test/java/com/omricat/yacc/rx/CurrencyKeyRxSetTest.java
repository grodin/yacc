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
import com.omricat.yacc.debug.TestPersister;
import com.omricat.yacc.model.CurrencyKey;
import com.omricat.yacc.rx.persistence.Persister;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.Set;

import rx.Observable;
import rx.functions.Func1;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith( MockitoJUnitRunner.class )
public class CurrencyKeyRxSetTest {

    static final Set<CurrencyKey> EMPTY_KEY_SET = Collections.emptySet();

    @Mock
    Persister<String, Set<CurrencyKey>> mockPersister;
    private final CurrencyKey usd = new CurrencyKey("USD");
    private final CurrencyKey gbp = new CurrencyKey("GBP");
    private final CurrencyKey eur = new CurrencyKey("EUR");
    private final CurrencyKey jpy = new CurrencyKey("JPY");
    private final Set<CurrencyKey> testKeySet = Sets.newHashSet(usd, eur, gbp);
    private final String persistenceKey = CurrencyKeyRxSet.PERSISTENCE_KEY;

    @Test( expected = NullPointerException.class )
    public void testCreateWithNull() throws Exception {
        CurrencyKeyRxSet.create(null);
    }

    @Test
    public void testJustCreatedIsEmpty() throws Exception {
        when(mockPersister.get(CurrencyKeyRxSet.PERSISTENCE_KEY))
                .thenReturn(Observable.<Set<CurrencyKey>>empty());

        Set<?> set = CurrencyKeyRxSet.create(mockPersister).get()
                .toBlocking().single();

        assertThat(set).isEmpty();
    }

    @Test
    public void testGet_EmptyPersister() throws Exception {
        when(mockPersister.get(persistenceKey))
                .thenReturn(Observable.<Set<CurrencyKey>>empty());

        Set<?> ret = CurrencyKeyRxSet.create(mockPersister).get()
                .toBlocking().single();

        assertThat(ret).isEmpty();

        verify(mockPersister).get(CurrencyKeyRxSet.PERSISTENCE_KEY);
    }

    @Test
    public void testGet_NonemptyPersister() throws Exception {
        when(mockPersister.get(persistenceKey))
                .thenReturn(Observable.just(testKeySet));

        Set<CurrencyKey> ret = CurrencyKeyRxSet.create(mockPersister).get()
                .toBlocking().single();

        assertThat(ret).containsAll(testKeySet);

    }

    @Test(expected = NullPointerException.class)
    public void testAdd_NullParam() throws Exception {
        CurrencyKeyRxSet.create(mockPersister).add(null);
    }

    @Test
    public void testAddBeforeGet_NonemptyPersister() throws Exception {

        final Persister<String, Set<CurrencyKey>> testPersister = new
                TestPersister<>();
        testPersister.put(persistenceKey, testKeySet);

        final Set<CurrencyKey> ret2 = CurrencyKeyRxSet.create(testPersister)
                .add(eur)
                .toBlocking().single();

        assertThat(ret2).containsAll(testKeySet).contains(eur);
    }

    @Test(expected = NullPointerException.class)
    public void testAddAll_NullParam() throws Exception {
        CurrencyKeyRxSet.create(mockPersister).addAll(null);
    }

    @Test
    public void testAddAll() throws Exception {

        final TestPersister<Set<CurrencyKey>> testPersister = new
                TestPersister<>();

        final CurrencyKeyRxSet currencyKeyRxSet =
                CurrencyKeyRxSet.create(testPersister);

        Set<CurrencyKey> set = currencyKeyRxSet
                .add(jpy)
                .flatMap(new Func1<Set<CurrencyKey>,
                                    Observable<? extends Set<CurrencyKey>>>() {


                    @Override
                    public Observable<? extends Set<CurrencyKey>> call(final
                                                              Set<CurrencyKey>
                                                                     currencyKeys) {
                        return currencyKeyRxSet.addAll(testKeySet);
                    }
                 })
                .toBlocking().single();

        assertThat(set).containsAll(testKeySet).contains(jpy);

        assertThat(testPersister.get(persistenceKey)
                .toBlocking().single())
                .containsAll(testKeySet).contains(jpy);
    }

    @Test(expected = NullPointerException.class)
    public void testRemove_NullParam() throws Exception {
        CurrencyKeyRxSet.create(mockPersister).remove(null);
    }

    @Test
    public void testRemove_EltPresentInPersister() throws Exception {
        final Persister<String, Set<CurrencyKey>> testPersister = new
                TestPersister<>();
        testPersister.put(persistenceKey, testKeySet);

        final Set<CurrencyKey> ret = CurrencyKeyRxSet.create(testPersister)
                .remove(usd)
                .toBlocking().single();

        assertThat(ret).contains(eur,gbp).doesNotContain(usd);

        assertThat(testPersister.get(persistenceKey).toBlocking().single())
                .contains(eur,gbp).doesNotContain(usd);
    }

    @Test(expected = NullPointerException.class)
    public void testRemoveAll_NullParam() throws Exception {
        CurrencyKeyRxSet.create(mockPersister).removeAll(null);
    }


    @Test
    public void testRemoveAll() throws Exception {
        final Persister<String, Set<CurrencyKey>> testPersister = new
                TestPersister<>();
        testPersister.put(persistenceKey, testKeySet);

        final Set<CurrencyKey> ret = CurrencyKeyRxSet.create(testPersister)
                .removeAll(Sets.newHashSet(usd,jpy))
                .toBlocking().single();

        assertThat(ret).contains(eur,gbp).doesNotContain(usd);

        assertThat(testPersister.get(persistenceKey).toBlocking().single())
                .contains(eur,gbp).doesNotContain(usd);
    }
}
