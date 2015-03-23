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

package com.omricat.yacc.ui.events;

import android.content.Context;

import org.jetbrains.annotations.NotNull;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * * This class represents the events which in turn represent the various
 * lifecycle events that are relevant for views in the sense of MVP (i.e.
 * Fragments or Activities primarily)
 * <p>
 * The class furthermore uses a Java simulation of a sum type to represent the
 * different events. Therefore, this class is <emph>closed</emph> for extension
 * apart from those subclasses declared directly as nested classes of this
 * class.

 */
public abstract class ViewLifecycleEvent {

    private ViewLifecycleEvent() {
        // no instances other than the subclasses declared below
    }

    @NotNull
    public static ViewLifecycleEvent onAttach(@NotNull final Context
                                                                   context) {
        return new OnAttachEvent(checkNotNull(context));
    }

    @NotNull
    public static ViewLifecycleEvent onResume() {
        return new OnResumeEvent();
    }

    @NotNull
    public static ViewLifecycleEvent onDestroy() {
        return new OnDetachEvent();
    }

    abstract void accept(Matcher matcher);

    public void match(@NotNull Matcher matcher) {
        accept(checkNotNull(matcher));
    }

    public interface Matcher {

        public void matchOnAttach(@NotNull ViewLifecycleEvent.OnAttachEvent e);

        public void matchOnResume(@NotNull OnResumeEvent e);

        public void matchOnDetach(@NotNull OnDetachEvent e);

    }

    /**
     * OnCreateEvent
     */
    public static final class OnAttachEvent extends
            ViewLifecycleEvent {

        public final Context context;

        private OnAttachEvent(final Context context) {
            this.context = context;
        }

        @Override void accept(final Matcher matcher) {
            matcher.matchOnAttach(this);
        }

    }

    /**
     * OnResumeEvent
     */
    public static final class OnResumeEvent extends
            ViewLifecycleEvent {

        private OnResumeEvent() {
        }

        @Override void accept(final Matcher matcher) {
            matcher.matchOnResume(this);
        }

    }

    /**
     * OnDetachEvent
     */
    public static final class OnDetachEvent extends
            ViewLifecycleEvent {

        private OnDetachEvent() {
        }

        @Override void accept(final Matcher matcher) {
            matcher.matchOnDetach(this);
        }

    }
}
