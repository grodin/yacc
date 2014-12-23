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

package com.omricat.yacc.rx.persistence;

import com.omricat.yacc.model.CurrencyDataset;

import org.jetbrains.annotations.NotNull;

import rx.Observable;
import rx.functions.Func1;

import static com.google.common.base.Preconditions.checkNotNull;

public class IsDataStaleFunc implements Func1<CurrencyDataset,
        Observable<CurrencyDataset>> {

    private final Observable<CurrencyDataset> other;
    private final IsDataStalePredicate predicate;

    IsDataStaleFunc(@NotNull final Observable<CurrencyDataset> other,
                            final IsDataStalePredicate predicate) {
        this.predicate = predicate;
        this.other = checkNotNull(other);
    }

    public static IsDataStaleFunc create(@NotNull final
                                         Observable<CurrencyDataset>
                                                 other) {
        return new IsDataStaleFunc(other, IsDataStalePredicate.createDefault());
    }

    @NotNull
    @Override
    public Observable<CurrencyDataset> call(final CurrencyDataset
                                                        currencyDataset) {
        if (predicate.call(currencyDataset)) {
            // Data is stale
            return other;
        } else {
            // Data not stale, so re-wrap it
            return Observable.just(currencyDataset);
        }
    }


}
