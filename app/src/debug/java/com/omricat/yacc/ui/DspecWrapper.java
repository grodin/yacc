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


import org.lucasr.dspec.DesignSpec;

import info.metadude.android.typedpreferences.BooleanPreference;

public class DspecWrapper {

    private final BooleanPreference dspecGridVisible;
    private final BooleanPreference dspecKeylinesVisible;
    private final BooleanPreference dspecSpacingsVisible;
    private final DesignSpec designSpec;

    public DspecWrapper(final BooleanPreference dspecGridVisible,
                        final BooleanPreference dspecKeylinesVisible,
                        final BooleanPreference dspecSpacingsVisible,
                        final DesignSpec designSpec) {

        this.dspecGridVisible = dspecGridVisible;
        this.dspecKeylinesVisible = dspecKeylinesVisible;
        this.dspecSpacingsVisible = dspecSpacingsVisible;
        this.designSpec = designSpec;

        designSpec.setBaselineGridVisible(dspecGridVisible.get());
        designSpec.setKeylinesVisible(dspecKeylinesVisible.get());
        designSpec.setSpacingsVisible(dspecSpacingsVisible.get());
    }


    public boolean isBaselineGridVisible() {
        return designSpec.isBaselineGridVisible();
    }

    public DspecWrapper setBaselineGridVisible(boolean visible) {
        dspecGridVisible.set(visible);
        designSpec.setBaselineGridVisible(visible);
        return this;
    }

    public boolean areKeylinesVisible() {
        return designSpec.areKeylinesVisible();
    }

    public DspecWrapper setKeylinesVisible(boolean visible) {
        dspecKeylinesVisible.set(visible);
        designSpec.setKeylinesVisible(visible);
        return this;
    }

    public boolean areSpacingsVisible() {
        return designSpec.areSpacingsVisible();
    }

    public DspecWrapper setSpacingsVisible(boolean visible) {
        dspecSpacingsVisible.set(visible);
        designSpec.setSpacingsVisible(visible);
        return this;
    }
}
