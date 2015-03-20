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

package com.omricat.yacc.debug;

import com.google.common.collect.Sets;
import com.omricat.yacc.data.TestCurrencies;
import com.omricat.yacc.data.api.CurrenciesService;
import com.omricat.yacc.data.model.CurrencyDataset;

import rx.Observable;

/**
 * Implementation of CurrenciesService which just returns a static
 * CurrencyDataset wrapped in an Observable. This is to be used on the in the
 * debug version of the app, as a quick way of getting data into the ui.
 */
public class DebugCurrenciesService implements CurrenciesService {

    final CurrencyDataset dataset = new CurrencyDataset(Sets.newHashSet
            (TestCurrencies.currencies),1421847301L);

    @Override public Observable<CurrencyDataset> getAllCurrencies() {
        return Observable.just(dataset);
    }
}
