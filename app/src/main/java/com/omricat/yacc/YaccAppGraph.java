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

import com.omricat.yacc.common.rx.RxSet;
import com.omricat.yacc.data.model.Currency;
import com.omricat.yacc.data.model.CurrencyCode;
import com.omricat.yacc.di.qualifiers.AllCurrencies;
import com.omricat.yacc.di.qualifiers.SelectedCurrencies;
import com.omricat.yacc.domain.SourceCurrencyProvider;
import com.omricat.yacc.ui.converter.ConverterPresenter;

import java.util.Collection;

import rx.Observable;

public interface YaccAppGraph {
    void inject(YaccApp app);

    SourceCurrencyProvider sourceCurrency();

    @AllCurrencies
    Observable<? extends Collection<Currency>> allCurrencies();

    @SelectedCurrencies
    Observable<? extends Collection<Currency>> selectedCurrencies();

    RxSet<CurrencyCode> currencyCodeRxSet();

    ConverterPresenter converterPresenter();
}
