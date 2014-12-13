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

package com.omricat.yacc.backend;

public class Config {

    public static final String CURRENCY_DATA_ENDPOINT =
            "http://www.getexchangerates.com";
    public static final String LATEST_CURRENCY_FILENAME =
            "currencies-latest.json";
    public static final String BUCKET = "yacc-backend.appspot.com";
    public static final String CURRENCY_NAMES = "currency-names.json";
    public static final String CURRENCY_NAMES_ENDPOINT = "http://openexchangerates.org";

    public static final String OPENEXCHANGERATES_ORG_APP_ID = "29659946fdb9498e82d0da4be65c4d15";
}
