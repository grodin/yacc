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
import rx.functions.Func0;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith( MockitoJUnitRunner.class )
public class EmptyFallbackTransformerTest {

    @Mock
    Func0<Observable<String>> fallbackTransformerFactory;

    private final EmptyFallbackTransformer<String> fallbackTransformer =
            EmptyFallbackTransformer.getInstance(Observable.just("fallback"));

    @Test(expected = NullPointerException.class)
    public void testConstructorNonLazy_NullParam() throws Exception {
        EmptyFallbackTransformer.getInstance(null);
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorLazy_NullParam() throws Exception {
        EmptyFallbackTransformer.getLazyInstance(null);
    }

    @Test
    public void testNonLazyCall_EmptyObservable() throws Exception {

        final String ret = Observable.<String>empty()
                .compose(fallbackTransformer)
                .toBlocking()
                .single();

        assertThat(ret).isEqualTo("fallback");
    }

    @Test
    public void testLazyCall_EmptyObservable() throws Exception {

        when(fallbackTransformerFactory.call()).thenReturn(
                Observable.just("factory fallback"));

        final String ret = Observable.<String>empty()
                .compose(EmptyFallbackTransformer
                            .getLazyInstance(fallbackTransformerFactory))
                .toBlocking()
                .single();

        verify(fallbackTransformerFactory).call();

        assertThat(ret).isEqualTo("factory fallback");
    }

    @Test
    public void testCall_NonEmptyObservable() throws Exception {
        final String ret = Observable.just("non-empty")
                .compose(fallbackTransformer)
                .toBlocking()
                .single();

        assertThat(ret).isEqualTo("non-empty");
    }
}
