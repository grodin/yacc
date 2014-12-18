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

import com.google.common.math.LongMath;
import com.omricat.yacc.model.CurrencyDataset;

import org.jetbrains.annotations.NotNull;

import java.math.RoundingMode;

import rx.Observable;
import rx.functions.Func0;
import rx.functions.Func1;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

class IsDataStaleFunc implements Func1<CurrencyDataset,
        Observable<CurrencyDataset>> {

    public static final int FIVE_MINS = 5 * 60;
    private final Observable<CurrencyDataset> other;
    private final Func0<Long> currentEpochFunc;

    IsDataStaleFunc(@NotNull final Observable<CurrencyDataset> other,
                    @NotNull final Func0<Long> currentEpochFunc) {
        this.other = checkNotNull(other);
        this.currentEpochFunc = checkNotNull(currentEpochFunc);
    }

    static IsDataStaleFunc create(@NotNull final Observable<CurrencyDataset>
                                          other,
                                  @NotNull final Func0<Long> currentEpochFunc) {
        return new IsDataStaleFunc(other, currentEpochFunc);
    }

    @NotNull
    @Override
    public Observable<CurrencyDataset> call(final CurrencyDataset currencyDataset) {
        final Long epoch = currentEpochFunc.call();
        checkArgument(epoch>=0,"epochFunc must always return non-negative");
        if (currencyDataset.getLastUpdatedTimestamp() + FIVE_MINS <
                epoch) {
            // Data is stale
            return other;
        } else {
            // Data not stale, so re-wrap it
            return Observable.just(currencyDataset);
        }
    }

    public static final Func0<Long> CURRENT_EPOCH_FUNC = new Func0<Long>() {
        @Override public Long call() {
            return LongMath.divide(System.currentTimeMillis(),1000,
                    RoundingMode.HALF_UP); // Round to nearest second
        }
    };

}
