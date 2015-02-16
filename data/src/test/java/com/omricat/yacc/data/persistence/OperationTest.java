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
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith( MockitoJUnitRunner.class )
public class OperationTest {

    @Mock
    Operation.Matcher<String> mockMatcher;

    @Test( expected = NullPointerException.class )
    public void testAdd_NullParam() throws Exception {
        Operation.add(null);
    }

    @Test( expected = NullPointerException.class )
    public void testRemove_NullParam() throws Exception {
        Operation.remove(null);
    }

    @Test( expected = NullPointerException.class )
    public void testAddAccept_NullParam() throws Exception {
        Operation.add("test").accept(null);
    }

    @Test( expected = NullPointerException.class )
    public void testRemoveAccept_NullParam() throws Exception {
        Operation.remove("test").accept(null);
    }

    @Test
    public void testAddMatch() throws Exception {
        Operation.match(
                Operation.add("add test"), mockMatcher);

        verify(mockMatcher).matchAdd(Matchers.<Operation.Add<String>>any());
    }

    @Test
    public void testRemoveMatch() throws Exception {
        Operation.match(
                Operation.remove("remove test"), mockMatcher);

        verify(mockMatcher).matchRemove(Matchers.<Operation
                .Remove<String>>any());
    }

    @Test
    public void testGetMatch() throws Exception {
        Operation.match(Operation.<String>get(), mockMatcher);

        verify(mockMatcher).matchGet(Matchers.<Operation.Get<String>>any());

    }
}
