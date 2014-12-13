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

package com.omricat.yacc.backend.util;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public class IOUtils {

    public static final int DEFAULT_BUFFER_SIZE = 4 * 1024;
    public static int EOF = -1;

    public static int copy(Reader input, Writer output) throws IOException {
        return copy(input,output,DEFAULT_BUFFER_SIZE);
    }

    public static int copy(Reader input, Writer output,
                           int bufferSize) throws IOException {
        return copy(input, output, new char[bufferSize]);
    }

    public static int copy(Reader input, Writer output, char[] buffer)
            throws IOException {
        int count = 0;
        int n = 0;
        while (EOF != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }

        return count;
    }

}
