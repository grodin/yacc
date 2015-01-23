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

package com.omricat.yacc.debug;

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.omricat.yacc.api.CurrenciesService;
import com.omricat.yacc.model.CurrencyDataset;

import java.io.IOException;

import rx.Observable;

/**
 * Implementation of CurrenciesService which just returns a static
 * CurrencyDataset wrapped in an Observable. This is to be used on the in the
 * debug version of the app, as a quick way of getting data into the ui.
 */
public class DebugCurrenciesService implements CurrenciesService {

    static final String currencyJson = "{\"currencies\":[{\"code\":\"TMT\"," +
            "\"value\":\"3.50\",\"name\":\"Turkmenistani Manat\"}," +
            "{\"code\":\"KZT\",\"value\":\"184.285\",\"name\":\"Kazakhstani " +
            "Tenge\"},{\"code\":\"BTN\",\"value\":\"61.615\"," +
            "\"name\":\"Bhutanese Ngultrum\"},{\"code\":\"CHF\"," +
            "\"value\":\"0.8615\",\"name\":\"Swiss Franc\"}," +
            "{\"code\":\"MDL\",\"value\":\"17.36\"," +
            "\"name\":\"Moldovan Leu\"},{\"code\":\"LVL\"," +
            "\"value\":\"0.6064\",\"name\":\"Latvian Lats\"}," +
            "{\"code\":\"CDF\",\"value\":\"927.00\",\"name\":\"Congolese " +
            "Franc\"},{\"code\":\"XDR\",\"value\":\"0.7038\"," +
            "\"name\":\"Special Drawing Rights\"},{\"code\":\"AUD\"," +
            "\"value\":\"1.2156\",\"name\":\"Australian Dollar\"}," +
            "{\"code\":\"SDG\",\"value\":\"5.6925\"," +
            "\"name\":\"Sudanese Pound\"},{\"code\":\"RUB\"," +
            "\"value\":\"65.8605\",\"name\":\"Russian Ruble\"}," +
            "{\"code\":\"VND\",\"value\":\"21380.00\",\"name\":\"Vietnamese " +
            "Dong\"},{\"code\":\"MUR\",\"value\":\"32.45\"," +
            "\"name\":\"Mauritian Rupee\"},{\"code\":\"JMD\"," +
            "\"value\":\"115.10\",\"name\":\"Jamaican Dollar\"}," +
            "{\"code\":\"LBP\",\"value\":\"1510.00\"," +
            "\"name\":\"Lebanese Pound\"},{\"code\":\"GBP\"," +
            "\"value\":\"0.6613\",\"name\":\"British Pound Sterling\"}," +
            "{\"code\":\"CAD\",\"value\":\"1.2069\"," +
            "\"name\":\"Canadian Dollar\"},{\"code\":\"MXN\"," +
            "\"value\":\"14.6014\",\"name\":\"Mexican Peso\"}," +
            "{\"code\":\"HUF\",\"value\":\"271.585\"," +
            "\"name\":\"Hungarian Forint\"},{\"code\":\"CNY\"," +
            "\"value\":\"6.2101\",\"name\":\"Chinese Yuan\"}," +
            "{\"code\":\"COP\",\"value\":\"2376.45\"," +
            "\"name\":\"Colombian Peso\"},{\"code\":\"HKD\"," +
            "\"value\":\"7.7529\",\"name\":\"Hong Kong Dollar\"}," +
            "{\"code\":\"JPY\",\"value\":\"117.367\"," +
            "\"name\":\"Japanese Yen\"},{\"code\":\"UZS\"," +
            "\"value\":\"2430.1699\",\"name\":\"Uzbekistan Som\"}," +
            "{\"code\":\"AZN\",\"value\":\"0.7831\",\"name\":\"Azerbaijani " +
            "Manat\"},{\"code\":\"INR\",\"value\":\"61.58\"," +
            "\"name\":\"Indian Rupee\"},{\"code\":\"CLF\"," +
            "\"value\":\"0.0247\",\"name\":\"Chilean Unit of Account (UF)\"}," +
            "{\"code\":\"EUR\",\"value\":\"0.8617\",\"name\":\"Euro\"}," +
            "{\"code\":\"IEP\",\"value\":\"0.6795\",\"name\":\"\"}," +
            "{\"code\":\"CZK\",\"value\":\"24.078\",\"name\":\"Czech Republic" +
            " Koruna\"},{\"code\":\"AMD\",\"value\":\"477.91\"," +
            "\"name\":\"Armenian Dram\"},{\"code\":\"XCD\"," +
            "\"value\":\"2.70\",\"name\":\"East Caribbean Dollar\"}," +
            "{\"code\":\"BYR\",\"value\":\"15090.00\",\"name\":\"Belarusian " +
            "Ruble\"},{\"code\":\"XOF\",\"value\":\"565.9925\"," +
            "\"name\":\"CFA Franc BCEAO\"},{\"code\":\"TWD\"," +
            "\"value\":\"31.492\",\"name\":\"New Taiwan Dollar\"}," +
            "{\"code\":\"USD\",\"value\":1,\"name\":\"United States " +
            "Dollar\"},{\"code\":\"ZAR\",\"value\":\"11.5518\"," +
            "\"name\":\"South African Rand\"},{\"code\":\"NOK\"," +
            "\"value\":\"7.6024\",\"name\":\"Norwegian Krone\"}]," +
            "\"timestamp\":1421847301}";

    final ObjectMapper mapper = new ObjectMapper();

    final CurrencyDataset dataset;

    public DebugCurrenciesService() {
        CurrencyDataset set = null;
        try {
            set = mapper.readValue(currencyJson,
                    CurrencyDataset.class);
        } catch (IOException e) {
            Log.e(this.getClass().getSimpleName(), e.getMessage());
        }
        if (set == null) {
            set = CurrencyDataset.EMPTY;
        }
        dataset = set;
    }


    @Override public Observable<CurrencyDataset> getAllCurrencies() {
        return Observable.just(dataset);
    }
}
