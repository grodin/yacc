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

package com.omricat.yacc.rx;

import com.google.common.collect.Lists;

import org.junit.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

import static org.assertj.core.api.Assertions.assertThat;

public class OperatorExponentialBackoffTest {

    List<Object> onNextCalls = Lists.newArrayList();
    List<Throwable> onErrorCalls = Lists.newArrayList();
    int timesSubscribed = 0;
    int retries = 0;
    private RuntimeException runtimeException;

    @Test( expected = NullPointerException.class )
    public void testConstructor_NullScheduler() throws Exception {
        new OperatorExponentialBackoff<>(0, null);
    }

    @Test( expected = IllegalArgumentException.class )
    public void testConstructor_NegativeMaxRetries() throws Exception {
        new OperatorExponentialBackoff<>(-1, Schedulers.test());
    }

    @Test
    public void testCall() throws Exception {
        final TestScheduler scheduler = Schedulers.test();
        final OperatorExponentialBackoff<Object> backoff = new
                OperatorExponentialBackoff<>(3, scheduler);


        Observable.create(new AlwaysErrorsOnSubscribe())
                .compose(backoff)
                .subscribe(new Action1<Object>() {
                    @Override public void call(final Object o) {
                        onNextCalls.add(o);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(final Throwable throwable) {
                        onErrorCalls.add(throwable);
                    }
                });

        // Subscribing is asynchronous, so make sure that we subscribe for
        // the first time
        scheduler.triggerActions();

        assertThat(timesSubscribed).isEqualTo(1);
        assertThat(retries).isEqualTo(0);

        scheduler.advanceTimeBy(1, TimeUnit.SECONDS);

        assertThat(retries).isEqualTo(1);
        assertThat(onNextCalls).hasSize(0);
        assertThat(onErrorCalls).hasSize(0);

        scheduler.advanceTimeBy(2, TimeUnit.SECONDS);

        assertThat(retries).isEqualTo(2);
        assertThat(onNextCalls).hasSize(0);
        assertThat(onErrorCalls).hasSize(0);

        scheduler.advanceTimeBy(4, TimeUnit.SECONDS);

        assertThat(retries).isEqualTo(3);
        assertThat(onNextCalls).hasSize(0);
        assertThat(onErrorCalls).hasSize(1);
        assertThat(onErrorCalls).containsOnly(runtimeException);
    }

    private class AlwaysErrorsOnSubscribe implements Observable.OnSubscribe<Object> {

        @Override
        public void call(final Subscriber<? super Object> subscriber) {
            timesSubscribed++;
            retries = Math.max(0, timesSubscribed - 1);
            runtimeException = new RuntimeException("Always calls onError");
            subscriber.onError(runtimeException);
        }
    }
}
