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

import com.omricat.yacc.data.PersistenceModule;
import com.omricat.yacc.data.model.Currency;
import com.omricat.yacc.data.model.CurrencyCode;
import com.omricat.yacc.data.model.CurrencyDataset;
import com.omricat.yacc.data.persistence.Persister;
import com.omricat.yacc.data.persistence.InMemoryPersister;

import java.util.Set;

import dagger.Module;

@Module
public class ReleasePersistenceModule implements PersistenceModule {

    Persister<String, Currency> provideCurrencyPersister() {
        return new InMemoryPersister<>();
    }

    Persister<String, Set<CurrencyCode>> provideCurrencyCodeSetPersister() {
        return new InMemoryPersister<>();
    }

    Persister<String, CurrencyDataset> provideCurrencyDatasetPersister() {
        return new InMemoryPersister<>();
    }

}
