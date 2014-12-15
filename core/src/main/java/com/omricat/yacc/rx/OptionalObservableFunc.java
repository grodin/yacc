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

import com.google.common.base.Optional;

import org.jetbrains.annotations.NotNull;

import rx.Observable;
import rx.functions.Func1;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Implementation of a {@link Func1} intended to be used in a call to {@link
 * Observable#flatMap(Func1)}.
 * <p/>
 * When {@link #call(Optional)} is called, if the {@code Optional<T>} contains a
 * value, that value is returned, wrapped in an {@code Observable}. Otherwise
 * the {@link Observable} parameter passed to the static {@link
 * #of(Observable)} factory method is returned.
 *
 * @param <T> the type contained in the {@code Optional<T>}
 */
public class OptionalObservableFunc<T> implements Func1<Optional<T>,
        Observable<? extends T>> {

    private final Observable<? extends T> observableForEmptyOptional;

    private OptionalObservableFunc(@NotNull final Observable<? extends T>
            observableT) {
        this.observableForEmptyOptional = checkNotNull(observableT);
    }

    /**
     * Static factory method to create an instance.
     *
     * @param observableForEmptyOptional {@code Observable} to use when the
     *                                   {@code Optional} is empty
     * @param <T>                        the type to be wrapped by the {@code
     *                                   OptionalObservable}
     */
    static <T> OptionalObservableFunc<T> of(@NotNull final Observable<? extends T>
                                                    observableForEmptyOptional) {
        return new OptionalObservableFunc<>(observableForEmptyOptional);
    }

    @Override
    public Observable<? extends T> call(final Optional<T> optional) {
        if (optional.isPresent()) {
            return Observable.just(optional.get());
        } else {
            return observableForEmptyOptional;
        }
    }


}
