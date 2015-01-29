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

package com.omricat.yacc.rx.persistence;

import org.jetbrains.annotations.NotNull;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class Operation<T> {

    public final T value;

    public abstract void accept(@NotNull final Visitor<T> visitor);

    private Operation(final T value) {
        this.value = value;
    }

    public static <T> Operation<T> add(@NotNull T value) {
        return Add.of(value);
    }

    public static <T> Operation<T> remove(@NotNull T value) {
        return Remove.of(value);
    }

    public final static class Add<T> extends Operation<T> {

        private Add(@NotNull final T value) {
            super(checkNotNull(value));
        }

        static <T> Add<T> of(@NotNull final T value) {
            return new Add<>(value);
        }

        @Override public void accept(@NotNull final Visitor<T> visitor) {
            checkNotNull(visitor).visit(this);
        }
    }

    public final static class Remove<T> extends Operation<T> {

        private Remove(@NotNull final T value) {
            super(checkNotNull(value));
        }

        static <T> Remove<T> of(@NotNull final T value) {
            return new Remove<>(value);
        }

        @Override public void accept(@NotNull final Visitor<T> visitor) {
            checkNotNull(visitor).visit(this);
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Operation operation = (Operation) o;

        if (!value.equals(operation.value)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    public interface Visitor<T> {
        public void visit(Add<T> add);
        public void visit(Remove<T> remove);
    }
}
