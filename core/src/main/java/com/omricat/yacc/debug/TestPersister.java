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

package com.omricat.yacc.debug;

import com.google.common.base.Optional;
import com.omricat.yacc.rx.persistence.Persister;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;

public class TestPersister<V> implements Persister<String,V> {

    protected Map<String,V> dataMap = new HashMap<>();

    @NotNull @Override
    public Observable<Optional<V>> get(@NotNull final java.lang.String key) {
        return Observable.just(Optional.fromNullable(dataMap.get(key)));
    }

    @NotNull @Override
    public Observable<V> put(@NotNull final java.lang.String key, @NotNull
    final V data) {
        dataMap.put(key,data);
        return Observable.just(data);
    }


}
