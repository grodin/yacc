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
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

public class ViewLifecycleEventTest {

    private boolean onCreateCalled;
    private boolean onResumeCalled;
    private boolean onDetachCalled;

    Context returnedContext;

    private final ViewLifecycleEvent.Matcher matcher = new
            ViewLifecycleEvent.Matcher() {
                @Override
                public void matchOnResume(@NotNull final
                                          ViewLifecycleEvent
                                                  .OnResumeEvent e) {
                    onResumeCalled = true;
                }

                @Override
                public void matchOnDetach(@NotNull final
                                          ViewLifecycleEvent
                                                  .OnDetachEvent e) {
                    onDetachCalled = true;
                }

                @Override
                public void matchOnAttach(@NotNull final
                                          ViewLifecycleEvent.OnAttachEvent e) {
                    onCreateCalled = true;
                    returnedContext = e.context;
                }
            };

    @Before
    public void setUp() throws Exception {
        onCreateCalled = false;
        onResumeCalled = false;
        onDetachCalled = false;
        returnedContext = null;
    }

    @Test( expected = NullPointerException.class )
    public void testOnCreate_NullParam() throws Exception {
        ViewLifecycleEvent.onAttach(null);
    }

    @Test
    public void testOnCreate() throws Exception {
        Context mockContext = Mockito.mock(Context.class);
        ViewLifecycleEvent e = ViewLifecycleEvent.onAttach(mockContext);

        assertThat(e).isNotNull();

        e.match(matcher);

        assertThat(onCreateCalled).isTrue();
        assertThat(returnedContext).isSameAs(mockContext);
        assertThat(onResumeCalled).isFalse();
        assertThat(onDetachCalled).isFalse();
    }

    @Test
    public void testOnResume() throws Exception {
        ViewLifecycleEvent e = ViewLifecycleEvent.onResume();

        assertThat(e).isNotNull();

        e.match(matcher);

        assertThat(onCreateCalled).isFalse();
        assertThat(onResumeCalled).isTrue();
        assertThat(onDetachCalled).isFalse();
    }

    @Test
    public void testOnDetach() throws Exception {
        ViewLifecycleEvent e = ViewLifecycleEvent.onDestroy();

        assertThat(e).isNotNull();

        e.match(matcher);

        assertThat(onCreateCalled).isFalse();
        assertThat(onResumeCalled).isFalse();
        assertThat(onDetachCalled).isTrue();
    }

    @Test( expected = NullPointerException.class )
    public void testMatch_NullParam() throws Exception {
        ViewLifecycleEvent.onDestroy().match(null);
    }
}
