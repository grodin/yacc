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

package com.omricat.yacc.backend.util;

import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.verify;

public class HttpUtilsTest {

    @Test(expected = NullPointerException.class)
    public void testSetJsonUTF8ContentType_NullParam() throws Exception {
        HttpUtils.setJsonUTF8ContentType(null);
    }

    @Test
    public void testSetJsonUTF8ContentType() throws Exception {

        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);

        HttpUtils.setJsonUTF8ContentType(resp);

        verify(resp).setContentType("application/json; charset=UTF-8");

    }
}
