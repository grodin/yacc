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

package com.omricat.yacc.model;

import com.google.common.collect.Sets;
import com.omricat.yacc.debug.InMemoryPersister;
import com.omricat.yacc.model.CurrencyCode;
import com.omricat.yacc.model.CurrencyCodeRxSet;
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
public class CurrencyCodeRxSetTest {

    static final Set<CurrencyCode> EMPTY_KEY_SET = Collections.emptySet();

    @Mock
    Persister<String, Set<CurrencyCode>> mockPersister;
    private final CurrencyCode usd = new CurrencyCode("USD");
    private final CurrencyCode gbp = new CurrencyCode("GBP");
    private final CurrencyCode eur = new CurrencyCode("EUR");
    private final CurrencyCode jpy = new CurrencyCode("JPY");
    private final Set<CurrencyCode> testKeySet = Sets.newHashSet(usd, eur, gbp);
    private final String persistenceKey = CurrencyCodeRxSet.PERSISTENCE_KEY;

    @Test( expected = NullPointerException.class )
    public void testCreateWithNull() throws Exception {
        CurrencyCodeRxSet.create(null);
    }

    @Test
    public void testJustCreatedIsEmpty() throws Exception {
        when(mockPersister.get(CurrencyCodeRxSet.PERSISTENCE_KEY))
                .thenReturn(Observable.<Set<CurrencyCode>>empty());

        Set<?> set = CurrencyCodeRxSet.create(mockPersister).get()
                .toBlocking().single();

        assertThat(set).isEmpty();
    }

    @Test
    public void testGet_EmptyPersister() throws Exception {
        when(mockPersister.get(persistenceKey))
                .thenReturn(Observable.<Set<CurrencyCode>>empty());

        Set<?> ret = CurrencyCodeRxSet.create(mockPersister).get()
                .toBlocking().single();

        assertThat(ret).isEmpty();

        verify(mockPersister).get(CurrencyCodeRxSet.PERSISTENCE_KEY);
    }

    @Test
    public void testGet_NonemptyPersister() throws Exception {
        when(mockPersister.get(persistenceKey))
                .thenReturn(Observable.just(testKeySet));

        Set<CurrencyCode> ret = CurrencyCodeRxSet.create(mockPersister).get()
                .toBlocking().single();

        assertThat(ret).containsAll(testKeySet);

    }

    @Test(expected = NullPointerException.class)
    public void testAdd_NullParam() throws Exception {
        CurrencyCodeRxSet.create(mockPersister).add(null);
    }

    @Test
    public void testAdd_NonemptyPersister() throws Exception {

        final Persister<String, Set<CurrencyCode>> testPersister = new
                InMemoryPersister<>();
        testPersister.put(persistenceKey, testKeySet);

        final Set<CurrencyCode> ret2 = CurrencyCodeRxSet.create(testPersister)
                .add(eur)
                .toBlocking().single();

        assertThat(ret2).containsAll(testKeySet).contains(eur);
    }

    @Test
    public void testAddThenGet() throws Exception {

        final Persister<String, Set<CurrencyCode>> testPersister = new
                InMemoryPersister<>();

        CurrencyCodeRxSet keySet = CurrencyCodeRxSet.create(testPersister);

        keySet.add(usd).toBlocking().single();

        final Set<CurrencyCode> ret = keySet.get().toBlocking().single();

        assertThat(ret).containsExactly(usd);
    }

    @Test(expected = NullPointerException.class)
    public void testAddAll_NullParam() throws Exception {
        CurrencyCodeRxSet.create(mockPersister).addAll(null);
    }

    @Test
    public void testAddAll() throws Exception {

        final InMemoryPersister<Set<CurrencyCode>> inMemoryPersister = new
                InMemoryPersister<>();

        final CurrencyCodeRxSet currencyCodeRxSet =
                CurrencyCodeRxSet.create(inMemoryPersister);

        Set<CurrencyCode> set = currencyCodeRxSet
                .add(jpy)
                .flatMap(new Func1<Set<CurrencyCode>,
                                    Observable<? extends Set<CurrencyCode>>>() {


                    @Override
                    public Observable<? extends Set<CurrencyCode>> call(final
                                                              Set<CurrencyCode>
                                                                     currencyKeys) {
                        return currencyCodeRxSet.addAll(testKeySet);
                    }
                 })
                .toBlocking().single();

        assertThat(set).containsAll(testKeySet).contains(jpy);

        assertThat(inMemoryPersister.get(persistenceKey)
                .toBlocking().single())
                .containsAll(testKeySet).contains(jpy);
    }

    @Test(expected = NullPointerException.class)
    public void testRemove_NullParam() throws Exception {
        CurrencyCodeRxSet.create(mockPersister).remove(null);
    }

    @Test
    public void testRemove_EltPresentInPersister() throws Exception {
        final Persister<String, Set<CurrencyCode>> testPersister = new
                InMemoryPersister<>();
        testPersister.put(persistenceKey, testKeySet);

        final Set<CurrencyCode> ret = CurrencyCodeRxSet.create(testPersister)
                .remove(usd)
                .toBlocking().single();

        assertThat(ret).contains(eur,gbp).doesNotContain(usd);

        assertThat(testPersister.get(persistenceKey).toBlocking().single())
                .contains(eur,gbp).doesNotContain(usd);
    }

    @Test(expected = NullPointerException.class)
    public void testRemoveAll_NullParam() throws Exception {
        CurrencyCodeRxSet.create(mockPersister).removeAll(null);
    }


    @Test
    public void testRemoveAll() throws Exception {
        final Persister<String, Set<CurrencyCode>> testPersister = new
                InMemoryPersister<>();
        testPersister.put(persistenceKey, testKeySet);

        final Set<CurrencyCode> ret = CurrencyCodeRxSet.create(testPersister)
                .removeAll(Sets.newHashSet(usd, jpy))
                .toBlocking().single();

        assertThat(ret).contains(eur,gbp).doesNotContain(usd);

        assertThat(testPersister.get(persistenceKey).toBlocking().single())
                .contains(eur,gbp).doesNotContain(usd);
    }
}
