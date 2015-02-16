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

package com.omricat.yacc;

import android.app.Application;
import android.content.Context;

import com.omricat.yacc.data.api.CurrenciesService;
import com.omricat.yacc.data.model.CurrencyCode;
import com.omricat.yacc.data.model.CurrencyDataset;
import com.omricat.yacc.data.persistence.Persister;
import com.omricat.yacc.debug.DebugCurrenciesService;
import com.omricat.yacc.debug.InMemoryPersister;
import com.omricat.yacc.domain.CurrencyCodeRxSet;
import com.omricat.yacc.domain.CurrencyDataRequester;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class YaccApp extends Application {

    private Persister<String, CurrencyDataset> currencyDatasetPersister;
    private Persister<String, Set<CurrencyCode>> selectedCurrenciesPersister;
    private CurrenciesService currenciesService;
    private CurrencyDataRequester currencyDataRequester;
    private CurrencyCodeRxSet currencyCodeRxSet;

    @NotNull
    public Persister<String, CurrencyDataset> getCurrencyDataPersister() {
        if (currencyDatasetPersister == null) {
            currencyDatasetPersister = new InMemoryPersister<>();
        }
        return currencyDatasetPersister;
    }

    @NotNull
    public Persister<String, Set<CurrencyCode>> getSelectedCurrenciesPersister
            () {
        if (selectedCurrenciesPersister == null) {
            selectedCurrenciesPersister = new InMemoryPersister<>();
        }
        return selectedCurrenciesPersister;
    }

    @NotNull
    public CurrenciesService getCurrenciesService() {
        if (currenciesService == null) {
            currenciesService = new DebugCurrenciesService();
        }
        return currenciesService;
    }

    @NotNull
    public CurrencyDataRequester getCurrencyDataRequester(@NotNull final
                                                          IsDataStalePredicate predicate) {
        if (currencyDataRequester == null) {
            currencyDataRequester = CurrencyDataRequester.create
                    (getCurrencyDataPersister(), getCurrenciesService(),
                            predicate);
        }
        return currencyDataRequester;
    }

    @NotNull
    public CurrencyCodeRxSet getCurrencyCodeRxSet() {
        if (currencyCodeRxSet == null) {
            currencyCodeRxSet = CurrencyCodeRxSet
                    .create(getSelectedCurrenciesPersister());
        }
        return currencyCodeRxSet;
    }

    @NotNull
    public static YaccApp from(Context context) {
        return (YaccApp) context.getApplicationContext();
    }

}
