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

package com.omricat.yacc.ui.selector;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.omricat.yacc.common.rx.RxSet;
import com.omricat.yacc.data.model.Currency;
import com.omricat.yacc.data.model.CurrencyCode;
import com.omricat.yacc.ui.events.ViewLifecycleEvent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.OngoingStubbing;

import java.util.Collection;
import java.util.Set;

import rx.Observable;
import rx.subjects.PublishSubject;

import static com.omricat.yacc.data.TestCurrencies.*;
import static com.omricat.yacc.data.TestCurrencyCodes.CAD_CODE;
import static com.omricat.yacc.data.TestCurrencyCodes.HKD_CODE;
import static org.mockito.Mockito.*;

@RunWith( MockitoJUnitRunner.class )
public class SelectorPresenterImplTest {

    Collection<Currency> currencies = ImmutableList.of(USD, AMD, KZT, CAD, HKD);

    Set<CurrencyCode> selectedCodes =
            ImmutableSet.of(HKD_CODE, CAD_CODE);

    Observable<? extends Collection<Currency>> allCurrencies =
            Observable.just(currencies);

    @Mock
    RxSet<CurrencyCode> selectedKeySet;

    @Mock
    SelectorView selectorView;

    SelectorPresenter classUnderTest;

    @Before
    public void setUp() throws Exception {
        when(selectorView.lifeCycleEvents())
                .thenReturn(Observable.<ViewLifecycleEvent>empty());
    }

    @Test( expected = NullPointerException.class )
    public void testAttachToView_NullView() throws Exception {
        new SelectorPresenterImpl(allCurrencies, selectedKeySet)
                .attachToView(null);


    }

    @Test
    public void testAttachToView() throws Exception {
        when(selectorView.selectionChangeEvents())
                .thenReturn(Observable.<CurrencySelectEvent>empty());

        whenSelectedKeySet(selectedKeySet.get())
                .thenReturn(Observable.just(selectedCodes));

        classUnderTest = new SelectorPresenterImpl(allCurrencies,
                selectedKeySet);

        classUnderTest.selectableCurrencies().toBlocking().first();

        verifyZeroInteractions(selectorView);

        classUnderTest.attachToView(selectorView).selectableCurrencies()
                .toBlocking().first();

        verify(selectorView).selectionChangeEvents();
        verify(selectorView).lifeCycleEvents();
    }

    @Test
    public void testDetachesFromViewOnLifecycleOnDestroy() throws Exception {
        when(selectorView.selectionChangeEvents())
                .thenReturn(Observable.<CurrencySelectEvent>empty());

        whenSelectedKeySet(selectedKeySet.get())
                .thenReturn(Observable.just(selectedCodes));

        PublishSubject<ViewLifecycleEvent> subject = PublishSubject.create();

        when(selectorView.lifeCycleEvents()).thenReturn(subject);

        classUnderTest = new SelectorPresenterImpl(allCurrencies,
                selectedKeySet).attachToView(selectorView);

        classUnderTest.selectableCurrencies().toBlocking().first();

        verify(selectorView).selectionChangeEvents();
        verify(selectorView).lifeCycleEvents();

        //Signal OnDestroy event
        subject.onNext(ViewLifecycleEvent.onDestroy());

        classUnderTest.selectableCurrencies().toBlocking().first();

        verifyNoMoreInteractions(selectorView);
    }

    /**
     * Helper method for stupid generics black magic.
     * Why didn't Java get a decent type system when it was designed? Grrrrr.
     */
    private static OngoingStubbing<Observable<? extends Set<CurrencyCode>>>
    whenSelectedKeySet(final Observable<? extends Set<CurrencyCode>> methodCall) {
        return Mockito.<Observable<? extends Set<CurrencyCode>>>when(methodCall);
    }
}
