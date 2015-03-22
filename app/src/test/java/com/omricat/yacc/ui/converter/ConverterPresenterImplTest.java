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
import com.omricat.yacc.ui.converter.events.ConverterViewLifecycleEvent;
import com.omricat.yacc.ui.converter.events.CurrencyValueChangeEvent;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Collection;

import rx.Observable;

import static com.omricat.yacc.data.TestCurrencies.*;
import static com.omricat.yacc.data.TestCurrencyCodes.USD_CODE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

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
                .thenReturn(Observable.<ConverterViewLifecycleEvent>empty());

    }

    @Test( expected = NullPointerException.class )
    public void testAttachToView_NullView() throws Exception {
        new ConverterPresenterImpl(sourceCurrencyProvider, currencies)
                .attachToView(null);
    }

    @Test
    public void testAttachToView() throws Exception {


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
                sourceCurrencyProvider, currencies).attachToView(converterView);


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
                sourceCurrencyProvider, currencies).attachToView(converterView);


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

        classUnderTest = new ConverterPresenterImpl(provider, currencies)
                .attachToView(converterView);

        Currency ret = classUnderTest.sourceCurrency()
                .first()
                .toBlocking().single();

        assertThat(ret).isEqualTo(USD);

    }
}
