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

import com.omricat.yacc.R;
import com.omricat.yacc.ui.converter.ConverterFragment;
import com.omricat.yacc.ui.selector.CurrencySelectionFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class YaccMainActivity extends YaccActivity {

    public static final String MAIN_CURRENCY_FRAGMENT = "MainCurrencyFragment";
    public static final String CURRENCY_SELECTION_FRAGMENT =
            "CurrencySelectionFragment";
    @InjectView(R.id.appbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currencies);

        ButterKnife.inject(this);

        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container,
                    new ConverterFragment(), MAIN_CURRENCY_FRAGMENT).commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_edit:
                return actionEdit();
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

    private boolean actionEdit() {
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
        return true;
    }

}
