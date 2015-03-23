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

import com.omricat.yacc.data.model.CurrencyCode;

import org.jetbrains.annotations.NotNull;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class CurrencySelectEvent {

    private final CurrencyCode code;

    private CurrencySelectEvent(final CurrencyCode code) {
        this.code = code;
    }

    @NotNull
    public final CurrencyCode code() {
        return code;
    }

    public interface Matcher<R> {
        public R matchSelectEvent(SelectEvent event);

        public R matchUnselectEvent(UnselectEvent event);

    }

    abstract <R> R accept(@NotNull final Matcher<R> matcher);

    /**
     * Select Event
     */


    @NotNull
    public static CurrencySelectEvent selectEvent(@NotNull final
                                                      CurrencyCode code) {
        return new SelectEvent(checkNotNull(code));
    }

    public static final class SelectEvent extends CurrencySelectEvent {


        private SelectEvent(CurrencyCode code) {
            super(code);
        }

        @Override <R> R accept(@NotNull final Matcher<R> matcher) {
            return matcher.matchSelectEvent(this);
        }


    }

    /**
     * Unselect Event
     */

    @NotNull
    public static CurrencySelectEvent unselectEvent(@NotNull final
                                                        CurrencyCode code) {
        return new UnselectEvent(checkNotNull(code));
    }

    public static final class UnselectEvent extends
            CurrencySelectEvent {

        private UnselectEvent(final CurrencyCode code) {
            super(code);
        }

        @Override <R> R accept(@NotNull final Matcher<R> matcher) {
            return matcher.matchUnselectEvent(this);
        }
    }
}
