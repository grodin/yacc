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

package com.omricat.yacc.backend.api;

import com.omricat.yacc.backend.Config;
import com.omricat.yacc.data.model.CurrencyCode;

import java.util.Map;

import retrofit.http.GET;

public interface NamesService {

    @GET( "/api/currencies.json?app_id=" + Config.OPENEXCHANGERATES_ORG_APP_ID )
    Map<CurrencyCode,String> getCurrencyNames();

}
