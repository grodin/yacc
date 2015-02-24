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

import com.omricat.yacc.YaccApp;
import com.omricat.yacc.data.model.ConvertedCurrency;
import com.omricat.yacc.data.model.Currency;
import com.omricat.yacc.data.persistence.Persister;
import com.omricat.yacc.ui.converter.events.ChooseCurrencyEvent;
import com.omricat.yacc.ui.converter.events.ConverterViewLifecycleEvent;
import com.omricat.yacc.ui.converter.events.CurrencyValueChangeEvent;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.Collection;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

import static com.omricat.yacc.ui.converter.events.ConverterViewLifecycleEvent.*;

public class ConverterPresenterImpl implements ConverterPresenter {

    public static final String PERSISTENCE_KEY = "last-currency-to-convert-from";



    private final Persister<String, Currency> sourceCurrencyPersister;

    // Observables from the domain layer

    private Observable<? extends Collection<Currency>> currencies;
    private Observable<Currency> lastSourceCurrency;

    // Observables from the view

    private Observable<ChooseCurrencyEvent> chooseCurrencyEvents;
    private Observable<CurrencyValueChangeEvent> valueChangeEvents;

    // Subscriptions

    private Subscription lifecycleSubscription;
    private Subscription subscription;

    private final ConverterViewLifecycleEvent.Matcher lifecycleEventMatcher =
            new ConverterViewLifecycleEvent.Matcher() {
                @Override
                public void matchOnAttach(@NotNull final OnAttachEvent e) {
                    YaccApp.from(e.context);
                }

                @Override
                public void matchOnResume(@NotNull final OnResumeEvent e) {
                }

                @Override
                public void matchOnDetach(@NotNull final OnDetachEvent e) {
                    subscription.unsubscribe();
                    lifecycleSubscription.unsubscribe();
                }
            };

    public ConverterPresenterImpl(final ConverterView view,
                                  final Persister<String, Currency> sourceCurrencyPersister) {


        this.sourceCurrencyPersister = sourceCurrencyPersister;

        chooseCurrencyEvents = view.chooseCurrencyEvents();
        valueChangeEvents = view.convertFromValueChangeEvents();

        lifecycleSubscription = view.lifecycleEvents().subscribe(
                new Action1<ConverterViewLifecycleEvent>() {
                    @Override
                    public void call(final ConverterViewLifecycleEvent event) {
                        event.match(lifecycleEventMatcher);
                    }
                }
        );

    }

    private Observable<Currency> getSourceCurrency() {
        return chooseCurrencyEvents
                .map(new Func1<ChooseCurrencyEvent, Currency>() {


                    @Override
                    public Currency call(final ChooseCurrencyEvent e) {
                        return e.chosenCurrency;
                        }
                    })
                .mergeWith(lastSourceCurrency)
                .flatMap(new Func1<Currency, Observable<Currency>>() {
                    @Override
                    public Observable<Currency> call(final Currency currency) {
                        return sourceCurrencyPersister
                                .put(PERSISTENCE_KEY, currency);
                    }
                });
    }

    private Observable<BigDecimal> sourceCurrencyValue() {
        return valueChangeEvents
                .map(new Func1<CurrencyValueChangeEvent, BigDecimal>() {
                    @Override
                    public BigDecimal call(final CurrencyValueChangeEvent e) {
                        return e.value;
                    }
                });
    }

    private Observable<? extends Collection<ConvertedCurrency>>
    getConvertedCurrencies() {

        return currencies
                .flatMap(new Func1<Collection<Currency>,
                        Observable<? extends Collection<ConvertedCurrency>>>() {



                    @Override
                    public Observable<? extends Collection<ConvertedCurrency>>
                    call
                            (final Collection<Currency> currencies) {
                        return Observable.from(currencies)
                                .flatMap(convertCurrencyFunc)
                                .toList();
                    }
                });
    }

    private final Func1<Currency, Observable<? extends ConvertedCurrency>>
            convertCurrencyFunc = new Func1<Currency, Observable<?
            extends ConvertedCurrency>>() {


        @Override
        public Observable<? extends
                ConvertedCurrency> call(final
                                        Currency
                                                targetCurrency) {
            return getSourceCurrency()
                    .flatMap(new Func1<Currency, Observable<? extends
                            ConvertedCurrency>>() {


                        @Override
                        public Observable<? extends ConvertedCurrency> call
                                (final Currency sourceCurrency) {
                            return sourceCurrencyValue()
                                    .map(new Func1<BigDecimal,
                                            ConvertedCurrency>() {


                                        @Override
                                        public ConvertedCurrency call(final
                                                                      BigDecimal value) {
                                            return ConvertedCurrency
                                                    .convertFromTo
                                                            (sourceCurrency,
                                                                    targetCurrency,
                                                                    value);
                                        }
                                    });
                        }
                    });
        }
    };


    @Override public Observable<? extends Collection<ConvertedCurrency>>
    convertedCurrencies() {
        return getConvertedCurrencies();
    }

    @Override public Observable<Currency> sourceCurrency() {
        return getSourceCurrency();
    }

}
