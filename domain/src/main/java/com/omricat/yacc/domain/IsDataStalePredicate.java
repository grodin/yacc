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

package com.omricat.yacc.domain;

import com.google.common.math.LongMath;
import com.omricat.yacc.common.rx.Predicate;
import com.omricat.yacc.data.model.CurrencyDataset;

import org.jetbrains.annotations.NotNull;

import java.math.RoundingMode;

import rx.functions.Func0;

import static com.google.common.base.Preconditions.*;

public class IsDataStalePredicate implements Predicate<CurrencyDataset> {

    public static final long FIVE_MINS = 5 * 60;

    private final Func0<Long> currentEpochFunc;
    private final long staleInterval;

    private IsDataStalePredicate(@NotNull final Func0<Long> currentEpochFunc,
                                 final long staleInterval) {
        checkArgument(staleInterval > 0, "Stale interval must be positive");
        checkArgument(currentEpochFunc.call() >= 0, "epoch Func0 must always " +
                "return non-negative");
        this.staleInterval = staleInterval;
        this.currentEpochFunc = checkNotNull(currentEpochFunc);
    }

    public static IsDataStalePredicate createDefault() {
        return new IsDataStalePredicate(CURRENT_EPOCH_FUNC, FIVE_MINS);
    }

    public static IsDataStalePredicate create(@NotNull final Func0<Long>
                                                      currentEpochFunc) {
        return new IsDataStalePredicate(currentEpochFunc, FIVE_MINS);
    }

    public static IsDataStalePredicate create(@NotNull final Func0<Long>
                                                      currentEpochFunc,
                                              final long staleInterval) {
        return new IsDataStalePredicate(currentEpochFunc, staleInterval);
    }


    @Override public Boolean call(final CurrencyDataset currencyDataset) {
        final Long epoch = currentEpochFunc.call();
        checkState(epoch >= 0, "epochFunc passed at creation must always " +
                "return non-negative");
        return (currencyDataset.getLastUpdatedTimestamp() + staleInterval) <
                epoch;
    }

    public static final Func0<Long> CURRENT_EPOCH_FUNC = new Func0<Long>() {
        @Override public Long call() {
            return LongMath.divide(System.currentTimeMillis(), 1000,
                    RoundingMode.HALF_UP); // Round to nearest second
        }
    };
}
