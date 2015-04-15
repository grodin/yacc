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

package com.omricat.yacc.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.omricat.yacc.R;
import com.omricat.yacc.ui.converter.ConverterFragment;
import com.omricat.yacc.ui.selector.CurrencySelectionFragment;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class YaccMainActivity extends YaccActivity implements MainView {

    public static final String MAIN_CURRENCY_FRAGMENT = "MainCurrencyFragment";
    public static final String CURRENCY_SELECTION_FRAGMENT =
            "CurrencySelectionFragment";

    private MainViewComponent component;

    @InjectView(R.id.appbar)
    Toolbar toolbar;

    @Inject
    MainPresenter presenter;

    @Inject
    ActivityContainer activityContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        component = MainViewComponent.Initializer.init(this);
        component.inject(this);

        final ViewGroup container = activityContainer.get(this);

        getLayoutInflater().inflate(R.layout.activity_main, container);

        ButterKnife.inject(this);

        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container,
                    new ConverterFragment(), MAIN_CURRENCY_FRAGMENT).commit();
        }

    }

    @Override protected void onResume() {
        super.onResume();
        if (presenter != null) {
            presenter.onResume();
        }
    }

    @Override protected void onPause() {
        if (presenter != null) {
            presenter.onPause();
        }
        super.onPause();
    }

    @Override protected void onDestroy() {
        if (presenter != null) {
            presenter.onDispose();
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_yacc_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_settings:
                return actionSettings();
            default:
                return  super.onOptionsItemSelected(item);

        }
    }

    private boolean actionSettings() {
        // TODO: implement settings screen
        return true;
    }

    @Override
    public void showSelectorView() {
        Fragment editFragment = getSupportFragmentManager()
                .findFragmentByTag(CURRENCY_SELECTION_FRAGMENT);
        if (editFragment == null) {
            editFragment = new CurrencySelectionFragment();
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, editFragment,
                        CURRENCY_SELECTION_FRAGMENT)
                .addToBackStack(null)
                .commit();
    }
}
