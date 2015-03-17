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

import com.omricat.yacc.common.rx.Predicate;
import com.omricat.yacc.common.rx.RxSet;
import com.omricat.yacc.data.api.CurrenciesService;
import com.omricat.yacc.data.model.Currency;
import com.omricat.yacc.data.model.CurrencyCode;
import com.omricat.yacc.data.model.CurrencyDataset;
import com.omricat.yacc.data.persistence.Persister;

import java.util.Collection;
import java.util.Set;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import rx.Observable;

@Module
public class DomainModule {

    @Singleton @Provides
    SourceCurrency provideSourceCurrency(final Persister<String, Currency>
                                                 sourceCurrencyPersister,
                                         final RxSet<CurrencyCode>
                                                 selectedCurrencyCodes) {
        return new SourceCurrency(sourceCurrencyPersister,
                selectedCurrencyCodes);
    }

    @Singleton @Provides
    Observable<? extends Collection<Currency>> provideSelectedCurrencies
            (final RxSet<CurrencyCode> currencyCodeRxSet,
             final CurrencyDataRequester currencyDataRequester) {

        return new SelectedCurrenciesProvider(currencyCodeRxSet.get(),
                currencyDataRequester).getCurrencyData();
    }

    @Singleton @Provides RxSet<CurrencyCode> provideCurrencyRxSet(
            final Persister<String, Set<CurrencyCode>> persister) {
        return CurrencyCodeRxSet.create(persister);
    }

    @Singleton @Provides CurrencyDataRequester
    provideCurrencyDataRequester(final Persister<String, CurrencyDataset>
                                         datasetPersister,
                                 final CurrenciesService service,
                                 final Predicate<CurrencyDataset>
                                         dataStalePredicate) {

        return CurrencyDataRequester.create(datasetPersister, service,
                dataStalePredicate);
    }


}
