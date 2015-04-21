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

package com.omricat.yacc.ui.debug;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.omricat.yacc.BuildConfig;
import com.omricat.yacc.R;
import com.omricat.yacc.YaccApp;
import com.omricat.yacc.data.di.qualifiers.ApiEndpoint;
import com.omricat.yacc.data.di.qualifiers.DspecGridVisible;
import com.omricat.yacc.data.di.qualifiers.DspecKeylinesVisible;
import com.omricat.yacc.data.di.qualifiers.DspecSpacingsVisible;
import com.omricat.yacc.data.network.EnumAdapter;
import com.omricat.yacc.data.network.NetworkEndpoint;
import com.omricat.yacc.ui.ActivityContainer;
import com.omricat.yacc.ui.YaccMainActivity;

import org.lucasr.dspec.DesignSpecFrameLayout;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import info.metadude.android.typedpreferences.BooleanPreference;
import info.metadude.android.typedpreferences.StringPreference;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class DebugActivityContainer implements ActivityContainer {

    // Dspec related preferences
    private final BooleanPreference dspecGridVisible;
    private final BooleanPreference dspecKeylinesVisible;
    private final BooleanPreference dspecSpacingsVisible;

    // Network/Mock mode related preferences
    private final StringPreference networkEndpoint;

    // Main App
    private final YaccApp app;

    public DebugActivityContainer
            (@DspecGridVisible final BooleanPreference dspecGridVisible,
             @DspecKeylinesVisible final BooleanPreference dspecKeylinesVisible,
             @DspecSpacingsVisible final BooleanPreference
                     dspecSpacingsVisible,
             @ApiEndpoint final StringPreference networkEndpoint,
             final YaccApp app) {

        this.dspecGridVisible = dspecGridVisible;
        this.dspecKeylinesVisible = dspecKeylinesVisible;
        this.dspecSpacingsVisible = dspecSpacingsVisible;
        this.networkEndpoint = networkEndpoint;
        this.app = app;
    }

    // Dspec layout
    @InjectView( R.id.debug_content )
    DesignSpecFrameLayout dspec;

    // Drawer widgets

    // Dspec related widgets
    @InjectView( R.id.debug_dspec_show_grid )
    Switch showDspecGridView;
    @InjectView( R.id.debug_dspec_show_keylines )
    Switch showDspecKeylinesView;
    @InjectView( R.id.debug_dspec_show_spacings )
    Switch showDspecSpacingsView;

    // Build info widgets
    @InjectView( R.id.debug_info_version_code )
    TextView infoVersionView;
    @InjectView( R.id.debug_info_version_number )
    TextView infoVersionNumberView;
    @InjectView( R.id.debug_info_commit_id )
    TextView infoCommitIdView;
    @InjectView( R.id.debug_info_build_datetime )
    TextView infoBuildDateTimeView;

    // Network widgets
    @InjectView( R.id.debug_network_endpoint )
    Spinner endpointView;


    @Override public ViewGroup get(final Activity activity) {

        activity.setContentView(R.layout.activity_currencies_debug_container);

        ButterKnife.inject(this, activity);

        setupNetworkSection(activity);
        setupUiSection();
        setupBuildInfoSection();

        return dspec;
    }

    private void setupNetworkSection(final Context context) {
        final NetworkEndpoint currentEndpoint =
                NetworkEndpoint.from(networkEndpoint.get());
        final EnumAdapter<NetworkEndpoint> endpointAdapter =
                new EnumAdapter<>(context, EndpointViewHolder.class,
                        NetworkEndpoint.class);
        endpointView.setAdapter(endpointAdapter);
        endpointView.setSelection(currentEndpoint.ordinal());
        endpointView.setOnItemSelectedListener(new AdapterView
                .OnItemSelectedListener() {

            @Override
            public void onItemSelected(final AdapterView<?> parent,
                                       final View view, final int position,
                                       final long id) {
                NetworkEndpoint endpoint = endpointAdapter.getItem(position);
                if (endpoint != currentEndpoint) {
                    setEndpointAndRelaunch(endpoint.getEndpoint().getUrl());
                }
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {
            }
        });
    }

    private void setupBuildInfoSection() {
        infoVersionView.setText(BuildConfig.VERSION_NAME);
        infoVersionNumberView.setText(String.valueOf(BuildConfig.VERSION_CODE));
        infoCommitIdView.setText(BuildConfig.BUILDINFO_COMMIT_ID);
        infoBuildDateTimeView.setText(BuildConfig.BUILDINFO_BUILD_DATETIME);
    }

    private void setupUiSection() {
        showDspecGridView.setChecked(dspecGridVisible.get());
        showDspecGrid(dspecGridVisible.get());
        showDspecKeylinesView.setChecked(dspecKeylinesVisible.get());
        showDspecKeylines(dspecKeylinesVisible.get());
        showDspecSpacingsView.setChecked(dspecSpacingsVisible.get());
        showDspecSpacings(dspecSpacingsVisible.get());
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

    private void setEndpointAndRelaunch(String endpoint) {
        networkEndpoint.set(endpoint);

        Intent newApp = new Intent(app, YaccMainActivity.class);
        newApp.setFlags(FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK);
        app.startActivity(newApp);
        YaccApp.from(app).buildComponentAndInject();
    }
}
