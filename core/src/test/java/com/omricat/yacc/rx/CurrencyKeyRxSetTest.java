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
import com.omricat.yacc.model.CurrencyKey;

import org.junit.Test;

import java.util.Collection;
import java.util.Set;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import rx.Observable;

import static org.assertj.core.api.Assertions.assertThat;

public class CurrencyKeyRxSetTest {

    private final CurrencyKey usd = new CurrencyKey("USD");
    private final CurrencyKey gbp = new CurrencyKey("GBP");
    private final CurrencyKey eur = new CurrencyKey("EUR");
    private final Collection<CurrencyKey> keys = Sets.newHashSet(usd, eur, gbp);

    @Test( expected = NullPointerException.class )
    public void testCreateWithNull() throws Exception {
        CurrencyKeyRxSet.create(null);
    }

    @Test
    public void testCreateThenGet() throws Exception {
        Set<CurrencyKey> ret = CurrencyKeyRxSet.create(keys).get()
                .toBlocking().single();
        assertThat(ret).isNotNull().containsExactlyElementsOf(keys);
    }

    @Test
    public void testNoArgumentCreateReturnsEmptySet() throws Exception {
        Set<CurrencyKey> ret = CurrencyKeyRxSet.create()
                .get()
                .toBlocking().single();
        assertThat(ret).isNotNull().isEmpty();
    }

    @Test
    public void testAdd() throws Exception {
        Set<CurrencyKey> ret = CurrencyKeyRxSet.create()
                .add(usd)
                .toBlocking().single();
        assertThat(ret).isNotNull().contains(usd);
    }

    @Test
    public void testAddAll() throws Exception {
        Set<CurrencyKey> ret = CurrencyKeyRxSet.create()
                .addAll(keys)
                .toBlocking().single();
        assertThat(ret).isNotNull().containsExactlyElementsOf(keys);
    }

    @Test
    public void testRemove() throws Exception {
        Set<CurrencyKey> ret = CurrencyKeyRxSet.create(keys)
                .remove(usd)
                .toBlocking().single();
        assertThat(ret).isNotNull().contains(gbp, eur).doesNotContain(usd);
    }

    @Test
    public void testRemoveAll() throws Exception {
        Collection<CurrencyKey> keysToRemove = Sets.newHashSet(usd, eur);
        Set<CurrencyKey> ret = CurrencyKeyRxSet.create(keys)
                .removeAll(keysToRemove)
                .toBlocking().single();
        assertThat(ret).isNotNull().contains(gbp).doesNotContain(usd, eur);
    }

    @Test
    public void testAsObservable() throws Exception {
        Observable<CurrencyKey> keyObservable = CurrencyKeyRxSet.create(keys)
                .asObservable();
        assertThat(keyObservable.toList().toBlocking().single()).containsAll
                (keys);

    }

    @Test
    public void testEqualsContract() throws Exception {
        EqualsVerifier.forClass(CurrencyKeyRxSet.class).usingGetClass()
                .suppress(Warning.NULL_FIELDS).verify();
    }

    @Test
    public void testToString() throws Exception {
        String ret = CurrencyKeyRxSet.create(keys).toString();

        assertThat(ret).isEqualTo("CurrencyKeyRxSet{" +
                "keySet=" + keys.toString() +
                '}');
    }
}
