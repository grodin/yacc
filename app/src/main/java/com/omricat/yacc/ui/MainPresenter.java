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

import com.omricat.yacc.ui.converter.ConverterMenuEvent;
import com.omricat.yacc.ui.converter.ConverterPresenter;

import org.jetbrains.annotations.NotNull;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

import static com.google.common.base.Preconditions.checkNotNull;

public class MainPresenter {

    private final MainView view;

    private final ConverterPresenter converterPresenter;

    private boolean paused;

    private Subscription subscription;

    private ConverterMenuEvent.Matcher<Void> matcher =
            new ConverterMenuEvent.Matcher<Void>() {
                @Override public Void matchEditEvent() {
                    if (!paused) {
                        view.showSelectorView();
                    }
                    return null;
                }
            };

    public MainPresenter(@NotNull final ConverterPresenter
                                 converterPresenter,
                         @NotNull final MainView view) {

        this.view = checkNotNull(view);
        this.converterPresenter = checkNotNull(converterPresenter);
        paused = false;
    }

    private Observable<ConverterMenuEvent> menuEvents() {
        return converterPresenter.menuEvents()
                .observeOn(AndroidSchedulers.mainThread());
    }


    public void onResume() {
        paused = false;
        subscription = menuEvents().subscribe(
                new Action1<ConverterMenuEvent>() {

                    @Override
                    public void call(final ConverterMenuEvent event) {
                        event.performMatch(matcher);
                    }
                });
    }

    public void onPause() {
        paused = true;
        subscription.unsubscribe();
    }


    public void onDispose() {
        subscription.unsubscribe();
    }
}
