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

import com.omricat.yacc.data.model.CurrencyCode;
import com.omricat.yacc.common.rx.RxSet;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

import rx.Observable;
import rx.functions.Func1;

import static com.google.common.base.Preconditions.checkNotNull;

public class OpToCurrencyCode implements
        Func1<Operation<CurrencyCode>, Observable<? extends
                Set<CurrencyCode>>> {

    private final RxSet<CurrencyCode> rxSet;

    public OpToCurrencyCode(@NotNull final RxSet<CurrencyCode> rxSet) {
        this.rxSet = checkNotNull(rxSet);
    }

    @NotNull
    @Override
    public Observable<? extends Set<CurrencyCode>> call(final
                                                        Operation<CurrencyCode> op) {
        return new OpMatcher().call(op);
    }

    private final class OpMatcher implements Operation
            .Matcher<CurrencyCode> {

        private Observable<? extends Set<CurrencyCode>> returnObservable;

        private Observable<? extends Set<CurrencyCode>> call
                (Operation<CurrencyCode> op) {
            op.accept(this);
            return returnObservable;
        }

        @Override
        public void matchAdd(@NotNull final Operation.Add<CurrencyCode> op) {
            returnObservable = rxSet.add(op.value);
        }

        @Override
        public void matchRemove(@NotNull final Operation.Remove<CurrencyCode> op) {
            returnObservable = rxSet.remove(op.value);
        }

        @Override
        public void matchGet(@NotNull final Operation.Get<CurrencyCode> op) {
            returnObservable = rxSet.get();
        }


    }
}
