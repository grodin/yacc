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

package com.omricat.yacc.debug;

import com.omricat.yacc.model.CurrencyDataset;
import com.omricat.yacc.rx.persistence.Persister;

/**
 * Implementation of a {@link CurrencyDataset} {@link Persister} which is backed by
 * a Map, just to get something up and running. By default, is empty.
 */
public class TestCurrencyDatasetPersister extends TestPersister<CurrencyDataset> {


}
