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

package com.omricat.yacc.ui.selector;

import com.omricat.yacc.data.model.SelectableCurrency;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

import rx.Observable;

/**
 * Interface for the presenter for a SelecterView
 */
public interface SelectorPresenter {

    @NotNull
    public SelectorPresenter attachToView(@NotNull final SelectorView
                                                      selectorView);

    @NotNull
    public Observable<? extends Collection<SelectableCurrency>>
    selectableCurrencies();
}
