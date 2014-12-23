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

import com.google.common.base.Optional;

import org.junit.Test;

import rx.Observable;

import static org.assertj.core.api.Assertions.assertThat;

public class OptionalObservableFuncTest {

    final Observable<String> altObservable = Observable.just("Hello");

    @Test(expected = NullPointerException.class)
    public void testConstructionWithNull() throws  Exception {
        OptionalObservableFunc.of(null);
    }

    @Test
    public void testCallWithEmptyOptional() throws Exception {
        String ret = OptionalObservableFunc.of(altObservable).call(Optional
                .<String>absent()).toBlocking().single();
        assertThat(ret).isEqualTo("Hello");
    }

    @Test
    public void testCallWithFullOptional() throws Exception {
        String ret = OptionalObservableFunc.of(altObservable).call(Optional
                .of("Goodbye")).toBlocking().single();
        assertThat(ret).isEqualTo("Goodbye");

    }
}
