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

package com.omricat.yacc.ui.converter;


import com.google.common.collect.ImmutableSet;
import com.omricat.yacc.data.TestPersister;
import com.omricat.yacc.data.model.ConvertedCurrency;
import com.omricat.yacc.data.model.Currency;
import com.omricat.yacc.data.model.CurrencyCode;
import com.omricat.yacc.domain.SourceCurrencyProvider;
import com.omricat.yacc.ui.converter.events.ChooseCurrencyEvent;
import com.omricat.yacc.ui.converter.events.CurrencyValueChangeEvent;
import com.omricat.yacc.ui.events.ViewLifecycleEvent;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Collection;

import rx.Observable;
import rx.subjects.PublishSubject;

import static com.omricat.yacc.data.TestCurrencies.*;
import static com.omricat.yacc.data.TestCurrencyCodes.USD_CODE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class ConverterPresenterImplTest {

    @Mock
    ConverterView converterView = Mockito.mock(ConverterView.class);

    @Mock
    SourceCurrencyProvider sourceCurrencyProvider = Mockito.mock
            (SourceCurrencyProvider.class);
    ConverterPresenter classUnderTest;

    private final ImmutableSet<Currency> currenciesSet =
            ImmutableSet.of(USD, GBP, EUR);
    final Observable<? extends Collection<Currency>> currencies =
            Observable.<Collection<Currency>>just(currenciesSet);


    @Before
    public void setUp() throws Exception {
        when(converterView.lifecycleEvents())
                .thenReturn(Observable.<ViewLifecycleEvent>empty());
        when(converterView.menuEvents())
                .thenReturn(Observable.<ConverterMenuEvent>empty());

    }

    @Test(expected = NullPointerException.class)
    public void testAttachToView_NullView() throws Exception {
        new ConverterPresenterImpl(sourceCurrencyProvider, currencies, mainView)
                .attachToView(null, null);
    }

    @Test
    public void testAttachToView() throws Exception {
        when(converterView.chooseCurrencyEvents())
                .thenReturn(Observable.just(ChooseCurrencyEvent.of(USD)));
        when(converterView.valueChangeEvents())
                .thenReturn(Observable.just(CurrencyValueChangeEvent.of("1")));

        TestPersister<CurrencyCode> testPersister = new TestPersister<>();

        SourceCurrencyProvider provider = new
                SourceCurrencyProvider(testPersister, currencies);

        classUnderTest = new ConverterPresenterImpl(provider, currencies,
                mainView);

        classUnderTest.sourceCurrency()
                .toBlocking().first();

        classUnderTest.convertedCurrencies()
                .toBlocking().first();

        verifyZeroInteractions(converterView);

        //Attach Presenter to View
        classUnderTest.attachToView(converterView, null);

        classUnderTest.convertedCurrencies()
                .toBlocking().first();

        classUnderTest.sourceCurrency()
                .toBlocking().first();

        verify(converterView).chooseCurrencyEvents();
        verify(converterView).valueChangeEvents();
        verify(converterView).lifecycleEvents();
        verify(converterView).menuEvents();
    }

    @Test
    public void testDetachFromView_LifecycleOnDestroy() throws Exception {
        when(converterView.chooseCurrencyEvents())
                .thenReturn(Observable.just(ChooseCurrencyEvent.of(USD)));
        when(converterView.valueChangeEvents())
                .thenReturn(Observable.just(CurrencyValueChangeEvent.of("1")));

        TestPersister<CurrencyCode> testPersister = new TestPersister<>();

        SourceCurrencyProvider provider = new
                SourceCurrencyProvider(testPersister, currencies);

        PublishSubject<ViewLifecycleEvent> subject =
                PublishSubject.create();

        when(converterView.lifecycleEvents())
                .thenReturn(subject);

        classUnderTest = new ConverterPresenterImpl(provider, currencies, mainView)
                .attachToView(converterView, null);

        verify(converterView).lifecycleEvents();

        classUnderTest.sourceCurrency()
                .toBlocking().first();

        classUnderTest.convertedCurrencies()
                .toBlocking().first();

        verify(converterView).chooseCurrencyEvents();
        verify(converterView).valueChangeEvents();
        verify(converterView).menuEvents();

        subject.onNext(ViewLifecycleEvent.onDestroy());

        classUnderTest.sourceCurrency()
                .toBlocking().first();

        classUnderTest.convertedCurrencies()
                .toBlocking().first();

        verifyNoMoreInteractions(converterView);

    }

    @Test
    public void testConvertedCurrencies_EmptyValueEvent() throws Exception {
        when(converterView.chooseCurrencyEvents())
                .thenReturn(Observable.just(ChooseCurrencyEvent.of(USD)));

        when(converterView.valueChangeEvents())
                .thenReturn(Observable.just(CurrencyValueChangeEvent.of("")));

        when(sourceCurrencyProvider.getLatestSourceCurrency())
                .thenReturn(Observable.just(GBP));

        when(sourceCurrencyProvider.persist(any(Currency.class)))
                .thenReturn(Observable.just(GBP));

        ConvertedCurrency expected = ConvertedCurrency.convertFromTo(GBP,
                USD, BigDecimal.ONE);

        classUnderTest = new ConverterPresenterImpl(
                sourceCurrencyProvider, currencies, mainView).attachToView(converterView, null);


        Collection<ConvertedCurrency> ret = classUnderTest.convertedCurrencies()
                .toBlocking().last();

        assertThat(ret).isNotEmpty().hasSameSizeAs(currenciesSet);

        assertThat(ret.iterator().next()).isEqualTo(expected);

    }

    @Test
    public void testConvertedCurrencies() throws Exception {

        when(converterView.chooseCurrencyEvents())
                .thenReturn(Observable.just(ChooseCurrencyEvent.of(USD)));

        when(converterView.valueChangeEvents())
                .thenReturn(Observable.just(CurrencyValueChangeEvent.of("1")));

        when(sourceCurrencyProvider.getLatestSourceCurrency())
                .thenReturn(Observable.just(USD));

        when(sourceCurrencyProvider.persist(any(Currency.class)))
                .thenReturn(Observable.just(USD));

        classUnderTest = new ConverterPresenterImpl(
                sourceCurrencyProvider, currencies, mainView).attachToView(converterView, null);


        Collection<ConvertedCurrency> ret = classUnderTest.convertedCurrencies()
                .toBlocking().last();

        assertThat(ret).isNotEmpty().hasSameSizeAs(currenciesSet);
    }

    @Test
    public void testGetCurrencyStartsWithSourceCurrency() throws Exception {
        when(converterView.chooseCurrencyEvents())
                .thenReturn(Observable.just(ChooseCurrencyEvent.of(GBP)));

        TestPersister<CurrencyCode> testPersister = new TestPersister<>();

        testPersister.internalMap()
                .put(SourceCurrencyProvider.PERSISTENCE_KEY, USD_CODE);

        SourceCurrencyProvider provider = new
                SourceCurrencyProvider(testPersister, currencies);

        classUnderTest = new ConverterPresenterImpl(provider, currencies, mainView)
                .attachToView(converterView, null);

        Currency ret = classUnderTest.sourceCurrency()
                .first()
                .toBlocking().single();

        assertThat(ret).isEqualTo(USD);

    }
}
