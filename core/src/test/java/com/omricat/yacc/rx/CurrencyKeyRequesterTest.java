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
import static org.assertj.core.api.Fail.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith( MockitoJUnitRunner.class )
public class CurrencyKeyRequesterTest {

    static final Set<CurrencyKey> EMPTY_KEY_SET = Collections.emptySet();

    @Mock
    Persister<String, Set<CurrencyKey>> mockPersister;
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
        when(mockPersister.get(CurrencyKeyRequester.PERSISTENCE_KEY))
                .thenReturn(Observable.<Set<CurrencyKey>>empty());

        Set<?> set = CurrencyKeyRequester.create(mockPersister).get()
                .toBlocking().single();

        assertThat(set).isEmpty();
    }

    @Test
    public void testGet_EmptyPersister() throws Exception {
        when(mockPersister.get(persistenceKey))
                .thenReturn(Observable.<Set<CurrencyKey>>empty());

        Set<?> ret = CurrencyKeyRequester.create(mockPersister).get()
                .toBlocking().single();

        assertThat(ret).isEmpty();

        verify(mockPersister).get(CurrencyKeyRequester.PERSISTENCE_KEY);
    }

    @Test
    public void testGet_NonemptyPersister() throws Exception {
        when(mockPersister.get(persistenceKey))
                .thenReturn(Observable.just(keys));

        Set<CurrencyKey> ret = CurrencyKeyRequester.create(mockPersister).get()
                .toBlocking().single();

        assertThat(ret).containsAll(keys);

    }

    @Test(expected = NullPointerException.class)
    public void testAdd_NullParam() throws Exception {
        CurrencyKeyRequester.create(mockPersister).add(null);
    }

    @Test
    public void testAddBeforeGet_NonemptyPersister() throws Exception {
        final Set<CurrencyKey> set = Sets.newHashSet(usd,gbp);

        final Persister<String, Set<CurrencyKey>> testPersister = new
                TestPersister<>();
        testPersister.put(persistenceKey, set);

        final Set<CurrencyKey> ret2 = CurrencyKeyRequester.create(testPersister)
                .add(eur)
                .toBlocking().single();

        assertThat(ret2).containsAll(set).contains(eur);
    }

    @Test(expected = NullPointerException.class)
    public void testAddAll_NullParam() throws Exception {
        CurrencyKeyRequester.create(mockPersister).addAll(null);
    }

    @Test
    public void testAddAll() throws Exception {

        final TestPersister<Set<CurrencyKey>> testPersister = new
                TestPersister<>();

        final CurrencyKeyRequester currencyKeyRequester =
                CurrencyKeyRequester.create(testPersister);

        Set<CurrencyKey> set = currencyKeyRequester
                .add(jpy)
                .flatMap(new Func1<Set<CurrencyKey>,
                                    Observable<? extends Set<CurrencyKey>>>() {


                    @Override
                    public Observable<? extends Set<CurrencyKey>> call(final
                                                              Set<CurrencyKey>
                                                                     currencyKeys) {
                        return currencyKeyRequester.addAll(keys);
                    }
                 })
                .toBlocking().single();

        assertThat(set).containsAll(keys).contains(jpy);

        assertThat(testPersister.get(persistenceKey)
                .toBlocking().single())
                .containsAll(keys).contains(jpy);
    }

    @Test(expected = NullPointerException.class)
    public void testRemove_NullParam() throws Exception {
        CurrencyKeyRequester.create(mockPersister).remove(null);
    }

    @Test
    public void testRemove_EltPresent() throws Exception {
        // TODO: implement CurrencyKeyRequesterTest#testRemove()




        fail("Test not implemented");
    }

    @Test(expected = NullPointerException.class)
    public void testRemoveAll_NullParam() throws Exception {
        CurrencyKeyRequester.create(mockPersister).removeAll(null);
    }


    @Test
    public void testRemoveAll() throws Exception {
        // TODO: implement CurrencyKeyRequesterTest#testRemoveAll()
        fail("Test not implemented");
    }
}
