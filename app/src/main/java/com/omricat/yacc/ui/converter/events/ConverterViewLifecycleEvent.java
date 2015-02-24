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

package com.omricat.yacc.ui.converter.events;

import android.content.Context;

import com.omricat.yacc.ui.converter.ConverterView;

import org.jetbrains.annotations.NotNull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This class represents the events which in turn represent the various
 * lifecycle events that are relevant for an instance of {@link ConverterView}.
 * <p/>
 * The class furthermore uses a Java simulation of a sum type to represent the
 * different events. Therefore, this class is <emph>closed</emph> for extension
 * apart from those subclasses declared directly as nested classes of this
 * class.
 */
public abstract class ConverterViewLifecycleEvent {

    private ConverterViewLifecycleEvent() {
    }

    abstract void accept(Matcher matcher);

    public void match(@NotNull Matcher matcher) {
        accept(checkNotNull(matcher));
    }

    public interface Matcher {

        public void matchOnAttach(@NotNull OnAttachEvent e);

        public void matchOnResume(@NotNull OnResumeEvent e);

        public void matchOnDetach(@NotNull OnDetachEvent e);

    }

    /**
     * OnCreateEvent
     */
    public static final class OnAttachEvent extends
            ConverterViewLifecycleEvent {

        public final Context context;

        private OnAttachEvent(final Context context) {
            this.context = context;
        }

        @Override void accept(final Matcher matcher) {
            matcher.matchOnAttach(this);
        }

    }

    @NotNull
    public static ConverterViewLifecycleEvent onAttach(@NotNull final Context
                                                                   context) {
        return new OnAttachEvent(checkNotNull(context));
    }

    /**
     * OnResumeEvent
     */
    public static final class OnResumeEvent extends
            ConverterViewLifecycleEvent {

        private OnResumeEvent() {
        }

        @Override void accept(final Matcher matcher) {
            matcher.matchOnResume(this);
        }

    }

    @NotNull
    public static ConverterViewLifecycleEvent onResume() {
        return new OnResumeEvent();
    }

    /**
     * OnDetachEvent
     */
    public static final class OnDetachEvent extends
            ConverterViewLifecycleEvent {

        private OnDetachEvent() {
        }

        @Override void accept(final Matcher matcher) {
            matcher.matchOnDetach(this);
        }

    }

    @NotNull
    public static ConverterViewLifecycleEvent onDestroy() {
        return new OnDetachEvent();
    }
}
