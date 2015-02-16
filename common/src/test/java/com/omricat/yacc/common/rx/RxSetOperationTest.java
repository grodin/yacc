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

package com.omricat.yacc.common.rx;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith( MockitoJUnitRunner.class )
public class RxSetOperationTest {

    @Mock
    RxSetOperation.Matcher<String> mockMatcher;

    @Test( expected = NullPointerException.class )
    public void testAdd_NullParam() throws Exception {
        RxSetOperation.add(null);
    }

    @Test( expected = NullPointerException.class )
    public void testRemove_NullParam() throws Exception {
        RxSetOperation.remove(null);
    }

    @Test( expected = NullPointerException.class )
    public void testAddAccept_NullParam() throws Exception {
        RxSetOperation.add("test").accept(null);
    }

    @Test( expected = NullPointerException.class )
    public void testRemoveAccept_NullParam() throws Exception {
        RxSetOperation.remove("test").accept(null);
    }

    @Test
    public void testAddMatch() throws Exception {
        RxSetOperation.match(
                RxSetOperation.add("add test"), mockMatcher);

        verify(mockMatcher).matchAdd(Matchers.<RxSetOperation.Add<String>>any());
    }

    @Test
    public void testRemoveMatch() throws Exception {
        RxSetOperation.match(
                RxSetOperation.remove("remove test"), mockMatcher);

        verify(mockMatcher).matchRemove(Matchers.<RxSetOperation
                .Remove<String>>any());
    }

    @Test
    public void testGetMatch() throws Exception {
        RxSetOperation.match(RxSetOperation.<String>get(), mockMatcher);

        verify(mockMatcher).matchGet(Matchers.<RxSetOperation.Get<String>>any());

    }
}
