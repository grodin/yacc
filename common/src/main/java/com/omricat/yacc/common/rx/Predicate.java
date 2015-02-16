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

import rx.functions.Func1;

/**
 * Convenience interface to represent a predicate (a function which accepts a
 * value and returns a boolean).
 *
 * @param <T> type of the value which the predicate accepts
 */
public interface Predicate<T> extends Func1<T, Boolean> {
}
