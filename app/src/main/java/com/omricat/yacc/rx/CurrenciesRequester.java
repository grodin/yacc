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

package com.omricat.yacc.rx;

import com.google.common.base.Preconditions;
import com.omricat.yacc.data.CurrencySet;
import com.omricat.yacc.network.CurrenciesService;

import org.jetbrains.annotations.NotNull;

import rx.Observable;


/**
 * Immutable implementation of {@link com.omricat.yacc.network.CurrenciesService}
 * which wraps an existing implementation to add functionality. Planned
 * functionality includes retry with exponential back-off.
 */
public class CurrenciesRequester implements CurrenciesService {

    private final CurrenciesService service;

    /**
     * Wraps the passed in {@link com.omricat.yacc.network.CurrenciesService}
     * @param service instance of {@link com.omricat.yacc.network.CurrenciesService}
     *                to be wrapped. Cannot be null.
     */
    public CurrenciesRequester(@NotNull CurrenciesService service) {
        this.service = Preconditions.checkNotNull(service);
    }

    @Override
    public Observable<CurrencySet> getAllCurrencies() {
        //TODO: Add exponential back-off using RxJava retryWhen()
        return service.getAllCurrencies();
    }
}
