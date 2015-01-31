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

package com.omricat.yacc.backend.servlets;

import com.google.common.base.Optional;
import com.omricat.yacc.model.Currency;
import com.omricat.yacc.model.CurrencyCode;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Class to clean up the data provided by the currency data api. The data
 * includes some old codes for currencies and currencies no longer in use.
 * This class deals with these.
 */
class DataConsistencyProcessor {

    static final CurrencyCode ALB = new CurrencyCode("ALB");
    static final CurrencyCode ALL = new CurrencyCode("ALL");
    static final CurrencyCode IEP = new CurrencyCode("IEP");

    final private Map<CurrencyCode,String> names;

    DataConsistencyProcessor(@NotNull final Map<CurrencyCode, String> names) {
        this.names = checkNotNull(names);
    }

    /**
     * Method which processes a currency and fixes any problematic
     * data in it. The method returns the fixed up Currency wrapped in an
     * Optional. If the Optional is empty, this indicates that the method has
     * determined that the Currency should be filtered out of the final dataset.
     *
     * @param currency Currency instance to fix up, not null
     * @return the fixed up Currency wrapped in an Optional
     */
    @NotNull
    Optional<Currency> fixData(@NotNull final Currency currency) {
        final Currency curr = checkNotNull(currency);

        if (curr.getCode().equals(ALB)) {
            // the getexchangerates.com api returns ALB for Albanian Lek, which
            // really has the ISO 4217 code ALL. This means the name lookup
            // will have also failed and needs to be looked up again.
            final String newName = names.get(ALL);
            return Optional.of(new Currency(curr.getRateInUSD(),ALL,
                    newName,curr.getDescription()));

        } else if (curr.getCode().equals(IEP)) {
            // IEP is the ISO 4217 code for Irish pounds,
            // which have not been legal tender for some time,
            // but still seem to have exchange rates listed in some places.
            // In order to avoid confusion this might cause, filter it for now.
            return Optional.absent();

        } else {
            // Return Currency as is
            return Optional.of(curr);
        }
    }

}
