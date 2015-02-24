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

package com.omricat.yacc.ui.converter.events;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class CurrencyValueChangeEvent {

    public final BigDecimal value;

    private CurrencyValueChangeEvent(@NotNull final BigDecimal value) {
        this.value = value;
    }

    public static CurrencyValueChangeEvent of(@NotNull final BigDecimal value) {
        checkArgument(value.signum() >= 0, "value must be positive");
        return new CurrencyValueChangeEvent(checkNotNull(value));
    }
}
