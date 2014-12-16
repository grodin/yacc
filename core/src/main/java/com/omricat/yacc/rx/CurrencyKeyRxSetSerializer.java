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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.omricat.yacc.model.CurrencyKey;

import java.io.IOException;

import rx.functions.Action1;

public class CurrencyKeyRxSetSerializer extends JsonSerializer<CurrencyKeyRxSet> {

    @Override
    public void serialize(final CurrencyKeyRxSet value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException, JsonProcessingException {
        jgen.writeStartObject();
        jgen.writeArrayFieldStart(CurrencyKeyRxSet.SELECTED_KEYS);
        value.asObservable().subscribe(new Action1<CurrencyKey>() {
            @Override public void call(final CurrencyKey currencyKey) {
                try {
                    jgen.writeString(currencyKey.key);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        jgen.writeEndArray();
        jgen.writeEndObject();
    }
}
