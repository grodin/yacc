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

package com.omricat.yacc.data.persistence;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import rx.Observable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith( MockitoJUnitRunner.class )
public class SafePersisterTest {

    @Mock
    Persister<String,Integer> wrappedPersister;

    @Test(expected = NullPointerException.class)
    public void testConstructorMethod_NullParam() throws Exception {
        SafePersister.wrap(null);
    }

    @Test
    public void testPutReturnsAtMostOne_WrappedInfiniteStream() throws
            Exception {
        when(wrappedPersister.put(anyString(),anyInt()))
                .thenReturn(Observable.just(1, 2, 3).repeat());

        int count = SafePersister.wrap(wrappedPersister).put("",0)
                .count()
                .toBlocking().single();

        assertThat(count).isEqualTo(1);
    }

    @Test
    public void testGetReturnsAtMostOne_WrappedInfiniteStream() throws
            Exception {
        when(wrappedPersister.get(anyString()))
                .thenReturn(Observable.just(1).repeat());

        int count = SafePersister.wrap(wrappedPersister).get("")
                .count()
                .toBlocking().single();

        assertThat(count).isEqualTo(1);
    }

    @Test(expected = RuntimeException.class)
    public void testGetPassesThroughError() throws Exception {

        when(wrappedPersister.get(anyString()))
                .thenReturn(Observable.<Integer>error(new RuntimeException()));

        SafePersister.wrap(wrappedPersister).get("")
                .toBlocking().single();
    }
}
