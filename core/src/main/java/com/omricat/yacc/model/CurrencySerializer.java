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

package com.omricat.yacc.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class CurrencySerializer extends JsonSerializer<Currency> {

    @Override
    public void serialize(final Currency value, final JsonGenerator jgen,
                          final SerializerProvider provider) throws
            IOException, JsonProcessingException {
        jgen.writeStartObject();
        jgen.writeStringField(Currency.CODE, value.getCode().getKey());
        jgen.writeStringField(Currency.VALUE,
                value.getValueInUSD().toPlainString());
        jgen.writeStringField(Currency.NAME, value.getName());
        jgen.writeEndObject();
    }
}
