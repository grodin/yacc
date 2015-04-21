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

package com.omricat.yacc.data;

import com.omricat.yacc.common.rx.Predicate;
import com.omricat.yacc.data.model.CurrencyCode;
import com.omricat.yacc.data.model.CurrencyDataset;
import com.omricat.yacc.data.persistence.FallbackPersister;
import com.omricat.yacc.data.persistence.InMemoryPersister;
import com.omricat.yacc.data.persistence.Persister;

import java.util.Set;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ReleasePersistenceModule implements PersistenceModule {

    private <T> Persister<String,T> newDiskPersister() {
        // TODO: replace InMemoryPersister with an actual disk persister
        return new InMemoryPersister<>();
    }

    @Override @Provides @Singleton
    public Persister<String, CurrencyCode> provideCurrencyPersister() {
        return newDiskPersister();
    }

    @Override @Provides @Singleton
    public Persister<String, Set<CurrencyCode>>
    provideCurrencyCodeSetPersister() {
        return newDiskPersister();
    }

    @Override @Provides @Singleton
    public Persister<String, CurrencyDataset>
    provideCurrencyDatasetPersister(final Predicate<CurrencyDataset> predicate) {

        final Persister<String, CurrencyDataset> diskPersister =
                newDiskPersister();
        return new FallbackPersister<>(new InMemoryPersister<CurrencyDataset>(),
                diskPersister, predicate);
    }

}
