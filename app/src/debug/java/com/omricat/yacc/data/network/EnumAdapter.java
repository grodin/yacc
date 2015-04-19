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

package com.omricat.yacc.data.network;

import android.content.Context;

import uk.co.ribot.easyadapter.BaseEasyAdapter;
import uk.co.ribot.easyadapter.ItemViewHolder;

public class EnumAdapter<T extends Enum<T>> extends BaseEasyAdapter<T> {

    private final T[] enumValues;

    public EnumAdapter(final Context context, final Class<? extends
            ItemViewHolder> itemViewHolderClass, final Class<T> enumType) {
        super(context, itemViewHolderClass);
        enumValues = enumType.getEnumConstants();
    }

    @Override public int getCount() {
        return enumValues.length;
    }

    @Override public T getItem(final int pos) {
        return enumValues[pos];
    }

    @Override public long getItemId(final int position) {
        return position;
    }
}
