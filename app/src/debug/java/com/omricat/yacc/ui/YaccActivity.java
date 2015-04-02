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

import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.omricat.yacc.BuildConfig;
import com.omricat.yacc.R;
import com.omricat.yacc.debug.DebugUtils;

import org.lucasr.dspec.DesignSpec;
import org.lucasr.dspec.DesignSpecFrameLayout;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class YaccActivity extends ActionBarActivity {

    @InjectView( R.id.dspec )
    DesignSpecFrameLayout dspecFrame;

    @Override public void setContentView(final int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.inject(this);
    }

    @Override protected void onStart() {
        super.onStart();
        if (BuildConfig.DEBUG) {
            DebugUtils.riseAndShine(this);
        }
    }

    @Override public boolean onCreateOptionsMenu(final Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_debug, menu);
        final DesignSpec dspec = dspecFrame.getDesignSpec();
        menu.findItem(R.id.menu_dspec_show_grid)
                .setChecked(dspec.isBaselineGridVisible());
        menu.findItem(R.id.menu_dspec_show_keylines)
                .setChecked(dspec.areKeylinesVisible());
        menu.findItem(R.id.menu_dspec_show_spacings)
                .setChecked(dspec.areSpacingsVisible());
        return true;
    }

    @Override public boolean onOptionsItemSelected(final MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_dspec_show_grid:
                return showDspecGrid(item);
            case R.id.menu_dspec_show_keylines:
                return showDspecKeylines(item);
            case R.id.menu_dspec_show_spacings:
                return showDspecSpacings(item);
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private boolean toggleItem(final MenuItem item) {
        final boolean show = !item.isChecked();
        item.setChecked(show);
        return show;
    }

    private boolean showDspecGrid(final MenuItem item) {
        dspecFrame.getDesignSpec().setBaselineGridVisible(toggleItem(item));
        return true;
    }

    private boolean showDspecKeylines(final MenuItem item) {
        dspecFrame.getDesignSpec().setKeylinesVisible(toggleItem(item));
        return true;
    }

    private boolean showDspecSpacings(final MenuItem item) {
        dspecFrame.getDesignSpec().setSpacingsVisible(toggleItem(item));
        return true;
    }

}
