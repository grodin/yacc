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
import android.widget.Switch;

import com.omricat.yacc.R;
import com.omricat.yacc.data.di.qualifiers.DspecGridVisible;
import com.omricat.yacc.data.di.qualifiers.DspecKeylinesVisible;
import com.omricat.yacc.data.di.qualifiers.DspecSpacingsVisible;
import com.omricat.yacc.data.preferences.BooleanPreference;

import org.lucasr.dspec.DesignSpecFrameLayout;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnCheckedChanged;

public class DebugActivityContainer implements ActivityContainer {

    private final BooleanPreference dspecGridVisible;
    private final BooleanPreference dspecKeylinesVisible;
    private final BooleanPreference dspecSpacingsVisible;

    @InjectView( R.id.debug_content )
    DesignSpecFrameLayout dspec;

    @InjectView( R.id.debug_dspec_show_grid )
    Switch showDspecGridView;
    @InjectView( R.id.debug_dspec_show_keylines )
    Switch showDspecKeylinesView;
    @InjectView( R.id.debug_dspec_show_spacings )
    Switch showDspecSpacingsView;


    @Inject
    public DebugActivityContainer
            (@DspecGridVisible final BooleanPreference dspecGridVisible,
             @DspecKeylinesVisible final BooleanPreference dspecKeylinesVisible,
             @DspecSpacingsVisible final BooleanPreference
                     dspecSpacingsVisible) {

        this.dspecGridVisible = dspecGridVisible;
        this.dspecKeylinesVisible = dspecKeylinesVisible;
        this.dspecSpacingsVisible = dspecSpacingsVisible;
    }

    @Override public ViewGroup get(final Activity activity) {

        activity.setContentView(R.layout.activity_currencies_debug_container);

        ButterKnife.inject(this, activity);

        showDspecGridView.setChecked(dspecGridVisible.get());
        showDspecKeylinesView.setChecked(dspecKeylinesVisible.get());
        showDspecSpacingsView.setChecked(dspecSpacingsVisible.get());



        return dspec;
    }

    @OnCheckedChanged( R.id.debug_dspec_show_grid )
    void showDspecGrid(boolean show) {
        getDspec().setBaselineGridVisible(show);
    }

    @OnCheckedChanged( R.id.debug_dspec_show_keylines )
    void showDspecKeylines(boolean show) {
        getDspec().setKeylinesVisible(show);
    }

    @OnCheckedChanged( R.id.debug_dspec_show_spacings )
    void showDspecSpacings(boolean show) {
        getDspec().setSpacingsVisible(show);
    }

    DspecWrapper getDspec() {
        return new DspecWrapper(dspecGridVisible, dspecKeylinesVisible,
                dspecSpacingsVisible, dspec.getDesignSpec());
    }
}
