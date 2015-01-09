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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import rx.Observable;
import rx.functions.Func1;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith( MockitoJUnitRunner.class )
public class FallbackPersisterTest {

    private final Observable<Integer> two = Observable.just
            (2);
    private final Observable<Integer> emptyObservable = Observable
            .empty();
    private final Observable<Integer> zero = Observable.just
            (0);
    @Mock
    Persister<String, Integer> mockFirstPersister;

    @Mock
    Persister<String, Integer> mockSecondPersister;

    @Mock
    Func1<Integer, Boolean> mockPredicate;

    @Test( expected = NullPointerException.class )
    public void testConstructor_FirstArgNull() throws Exception {
        new FallbackPersister<>(null, mockSecondPersister);

    }

    @Test( expected = NullPointerException.class )
    public void testConstructor_SecondArgNull() throws Exception {
        new FallbackPersister<>(mockFirstPersister, null);
    }

    @Test( expected = NullPointerException.class )
    public void testConstructor_ThirdArgNull() throws Exception {
        new FallbackPersister<>(mockFirstPersister, mockSecondPersister, null);
    }

    @Test
    public void testGetCallsFirstPersister() throws Exception {
        when(mockFirstPersister.get(anyString())).thenReturn(two);

        Persister<String, Integer> persister =
                new FallbackPersister<>(mockFirstPersister,
                        mockSecondPersister);

        persister.get("Test").toBlocking().single();

        verify(mockFirstPersister).get("Test");
        verifyZeroInteractions(mockSecondPersister);
    }

    @Test
    public void testGetTestsPredicate() throws Exception {
        when(mockFirstPersister.get(anyString())).thenReturn(two);

        when(mockPredicate.call(anyInt())).thenReturn(true);

        Persister<String, Integer> persister =
                new FallbackPersister<>(mockFirstPersister,
                        mockSecondPersister, mockPredicate);

        persister.get("Test").toBlocking().single();

        verify(mockPredicate, atLeastOnce()).call(2);
    }

    @Test
    public void testGetFallsBackToSecondOne_EmptyObservable() throws Exception {
        when(mockFirstPersister.get(anyString())).thenReturn(emptyObservable);

        when(mockSecondPersister.get(anyString())).thenReturn(two);

        Persister<String, Integer> persister =
                new FallbackPersister<>(mockFirstPersister,
                        mockSecondPersister);

        persister.get("Test").toBlocking().single();

        verify(mockSecondPersister).get("Test");
    }

    @Test
    public void testFallsBackToSecond_PredicateFalse() throws Exception {
        when(mockFirstPersister.get(anyString())).thenReturn(zero);

        when(mockSecondPersister.get(anyString())).thenReturn(two);

        when(mockPredicate.call(anyInt())).thenReturn(false);

        Persister<String, Integer> persister =
                new FallbackPersister<>(mockFirstPersister,
                        mockSecondPersister, mockPredicate);

        int ret = persister.get("Test").toBlocking().single();

        verify(mockSecondPersister).get("Test");

        assertThat(ret).isEqualTo(2);
    }

    @Test
    public void testPutCallsPutOnPersisters() throws Exception {
        when(mockFirstPersister.put(anyString(), anyInt())).thenReturn
                (Observable.just(0));

        when(mockSecondPersister.put(anyString(), anyInt())).thenReturn
                (Observable.just(0));

        Persister<String, Integer> persister =
                new FallbackPersister<>(mockFirstPersister,
                        mockSecondPersister);

        persister.put("Test", 0).toBlocking().single();

        verify(mockFirstPersister).put("Test", 0);

        verify(mockSecondPersister).put("Test", 0);
    }


}
