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

package com.omricat.yacc.ui.selecter;

import org.junit.Test;

import static com.omricat.yacc.data.TestCurrencyCodes.USD_CODE;
import static com.omricat.yacc.ui.selecter.CurrencySelectEvent.*;
import static org.assertj.core.api.Assertions.assertThat;

public class CurrencySelectEventTest {

    private final String SELECT_EVENT = "Select Event";
    private final String UNSELECT_EVENT = "Unselect Event";
    Matcher<String> matcher =
            new Matcher<String>() {
                @Override
                public String matchSelectEvent(final SelectEvent event) {
                    return SELECT_EVENT;
                }

                @Override
                public String matchUnselectEvent(final UnselectEvent event) {
                    return UNSELECT_EVENT;
                }
            };

    @Test( expected = NullPointerException.class )
    public void testSelectEvent_NullParam() throws Exception {
        CurrencySelectEvent.selectEvent(null);
    }

    @Test
    public void testSelectEvent() throws Exception {

        String ret = CurrencySelectEvent.selectEvent(USD_CODE)
                .accept(matcher);

        assertThat(ret).isEqualTo(SELECT_EVENT);
    }

    @Test( expected = NullPointerException.class )
    public void testUnselectEvent_NullParam() throws Exception {
        CurrencySelectEvent.unselectEvent(null);
    }

    @Test
    public void testUnselectEvent() throws Exception {

        String ret = CurrencySelectEvent.unselectEvent(USD_CODE).accept
                (matcher);

        assertThat(ret).isEqualTo(UNSELECT_EVENT);
    }
}
