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

package com.omricat.yacc.rx;

import rx.Observable;

/**
 * Created by jsc on 15/12/14.
 */
public interface Persister<K> {

    /**
     * Return an observable which will return the object requested in {@code
     * onNext()} and then call {@code onCompleted()},
     * or if the object was not found, will call {@code onCompeleted()}
     * immediately.
     *
     * @param key
     * @param <T>
     * @return
     */
    <T> Observable<T> get(K key);
    <T> Observable<T> put(K key);

}