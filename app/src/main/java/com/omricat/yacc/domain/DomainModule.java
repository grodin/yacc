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
import com.omricat.yacc.data.network.CurrenciesService;
import com.omricat.yacc.data.model.Currency;
import com.omricat.yacc.data.model.CurrencyCode;
import com.omricat.yacc.data.model.CurrencyDataset;
import com.omricat.yacc.data.persistence.Persister;
import com.omricat.yacc.di.qualifiers.AllCurrencies;
import com.omricat.yacc.di.qualifiers.SelectedCurrencies;

import java.util.Collection;
import java.util.Set;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import rx.Observable;
import rx.functions.Func1;

@Module
public class DomainModule {

    @Singleton @Provides SourceCurrencyProvider
    provideSourceCurrency(final Persister<String, CurrencyCode>
                                  sourceCurrencyPersister,
                          @SelectedCurrencies
                          final Observable<? extends Collection<Currency>>
                                  selectedCurrencies) {

        return new SourceCurrencyProvider(sourceCurrencyPersister,
                selectedCurrencies);
    }

    @Singleton @Provides @SelectedCurrencies
    Observable<? extends Collection<Currency>> provideSelectedCurrencies
            (final RxSet<CurrencyCode> currencyCodeRxSet,
             final CurrencyDataRequester currencyDataRequester) {

        return new SelectedCurrenciesProvider(currencyCodeRxSet.get(),
                currencyDataRequester).getCurrencyData();
    }

    @Singleton @Provides @AllCurrencies
    Observable<? extends Collection<Currency>>
    provideAllCurrencies(final CurrencyDataRequester currencyDataRequester){

        return currencyDataRequester.request().map(new Func1<CurrencyDataset, Collection<Currency>>() {


            @Override
            public Collection<Currency> call(final CurrencyDataset
                                                     currencyDataset) {
                return currencyDataset.getCurrencies();
            }
        });
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
