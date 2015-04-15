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

package com.omricat.yacc.ui;

import android.app.Activity;
import android.view.ViewGroup;

import butterknife.ButterKnife;

/**
 * Abstracts getting the base container ViewGroup for an Activity,
 * which the Activity should inflate it's Views into.
 */
public interface ActivityContainer {

    ViewGroup get(Activity activity);

    public final static ActivityContainer DEFAULT = new ActivityContainer() {

        @Override public ViewGroup get(final Activity activity) {
            return ButterKnife.findById(activity, android.R.id.content);
        }
    };

}
