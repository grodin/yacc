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

package com.omricat.yacc;

import android.app.Application;
import android.content.Context;

import com.omricat.yacc.ui.ActivityHierarchyServer;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

public class YaccApp extends Application {

    @Inject
    ActivityHierarchyServer activityHierarchyServer;

    private YaccAppComponent component;

    @Override public void onCreate() {
        super.onCreate();

        component = YaccAppComponent.Initializer.init(this);
        component.inject(this);

        registerActivityLifecycleCallbacks(activityHierarchyServer);
    }

    @NotNull
    public YaccAppComponent component() {
        return component;
    }

    @NotNull
    public static YaccApp from(Context context) {
        return (YaccApp) context.getApplicationContext();
    }

}
