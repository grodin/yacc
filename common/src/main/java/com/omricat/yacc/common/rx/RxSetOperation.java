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

import org.jetbrains.annotations.NotNull;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class RxSetOperation<T> {

    public abstract void accept(@NotNull final Matcher<T> matcher);

    private RxSetOperation() {
    }

    public static <T> void match(@NotNull final RxSetOperation<T> op,
                                 @NotNull final Matcher<T> matcher) {
        op.accept(matcher);
    }

    public interface Matcher<T> {
        public void matchAdd(@NotNull Add<T> op);
        public void matchRemove(@NotNull Remove<T> op);
        public void matchGet(@NotNull Get<T> op);
    }

    public static <T> RxSetOperation<T> add(@NotNull T value) {
        return new Add<>(value);
    }

    public final static class Add<T> extends RxSetOperation<T> {

        public final T value;

        private Add(@NotNull final T value) {
            this.value = checkNotNull(value);
        }

        @Override public void accept(@NotNull final Matcher<T> matcher) {
            checkNotNull(matcher).matchAdd(this);
        }
    }

    public static <T> RxSetOperation<T> remove(@NotNull T value) {
        return new Remove<>(value);
    }

    public final static class Remove<T> extends RxSetOperation<T> {

        public final T value;

        private Remove(@NotNull final T value) {
            this.value = checkNotNull(value);
        }
        @Override public void accept(@NotNull final Matcher<T> matcher) {
            checkNotNull(matcher).matchRemove(this);
        }
    }

    public static <T> RxSetOperation<T> get() {
        return new Get<>();
    }

    public final static class Get<T> extends RxSetOperation<T> {

        @Override public void accept(@NotNull final Matcher<T> matcher) {
            checkNotNull(matcher).matchGet(this);
        }
    }


}
