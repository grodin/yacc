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

@RunWith( MockitoJUnitRunner.class )
public class FallbackPersisterTest {

    @Mock
    Persister<String, Integer> mockFirstPersister;

    @Mock
    Persister<String, Integer> mockSecondPersister;

    @SuppressWarnings( "unchecked" )
    @Test
    public void testConstructor_FirstArgNull() throws Exception {
        new FallbackPersister(null, mockSecondPersister);

    }

    @SuppressWarnings( "unchecked" )
    @Test
    public void testConstructor_SecondtArgNull() throws Exception {
        new FallbackPersister(mockFirstPersister, null);
    }


    @Test
    public void testGet() throws Exception {

    }

    @Test
    public void testPut() throws Exception {

    }
}
