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

import android.text.TextUtils;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.omricat.yacc.data.model.ConvertedCurrency;
import com.omricat.yacc.data.model.Currency;
import com.omricat.yacc.domain.SourceCurrencyProvider;
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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.omricat.yacc.ui.converter.events
        .ConverterViewLifecycleEvent.*;

public class ConverterPresenterImpl implements ConverterPresenter {

    // Domain layer objects

    private final SourceCurrencyProvider sourceCurrencyProvider;

    // Observables from the domain layer

    private final Observable<? extends Collection<Currency>> currencies;

    // Observables from the view

    private Observable<ChooseCurrencyEvent> chooseCurrencyEvents =
            Observable.empty();

    private Observable<CurrencyValueChangeEvent> valueChangeEvents =
            Observable.empty();

    // Subscriptions

    private Subscription lifecycleSubscription;

    // Attach status
    private boolean attachedToView = false;

    public ConverterPresenterImpl(@NotNull final SourceCurrencyProvider
                                          sourceCurrencyProvider,
                                  @NotNull final Observable<? extends
                                          Collection<Currency>> currencies) {
        this.sourceCurrencyProvider = checkNotNull(sourceCurrencyProvider);
        this.currencies = checkNotNull(currencies);
    }

    private final ConverterViewLifecycleEvent.Matcher lifecycleEventMatcher =
            new ConverterViewLifecycleEvent.Matcher() {
                @Override
                public void matchOnAttach(@NotNull final OnAttachEvent e) {
                }

                @Override
                public void matchOnResume(@NotNull final OnResumeEvent e) {
                }

                @Override
                public void matchOnDetach(@NotNull final OnDetachEvent e) {
                    detachView();
                }
            };

    private void attachView(final ConverterView view) {
        final ConverterView v = checkNotNull(view);
        chooseCurrencyEvents = v.chooseCurrencyEvents();
        valueChangeEvents = v.valueChangeEvents();

        lifecycleSubscription = v.lifecycleEvents().subscribe(
                new Action1<ConverterViewLifecycleEvent>() {
                    @Override
                    public void call(final ConverterViewLifecycleEvent event) {
                        event.match(lifecycleEventMatcher);
                    }
                }
        );

        attachedToView = true;
    }

    private void detachView() {
        lifecycleSubscription.unsubscribe();
        chooseCurrencyEvents = Observable.empty();
        valueChangeEvents = Observable.empty();
        attachedToView = false;
    }

    private Observable<Currency> getSourceCurrency() {
        return chooseCurrencyEvents
                .map(new Func1<ChooseCurrencyEvent, Currency>() {


                    @Override
                    public Currency call(final ChooseCurrencyEvent e) {
                        return e.chosenCurrency;
                    }
                })
                .startWith(sourceCurrencyProvider.getLatestSourceCurrency())
                .flatMap(new Func1<Currency, Observable<Currency>>() {
                    @Override
                    public Observable<Currency> call(final Currency currency) {
                        return sourceCurrencyProvider.persist(currency);
                    }
                });
    }

    private Observable<BigDecimal> sourceCurrencyValue() {
        return valueChangeEvents
                .map(new Func1<CurrencyValueChangeEvent, BigDecimal>() {
                    @Override
                    public BigDecimal call(final CurrencyValueChangeEvent e) {

                        return TextUtils.isEmpty(e.value) ?
                                BigDecimal.ONE :
                                new BigDecimal(e.value);
                    }
                }).startWith(BigDecimal.ONE);
    }

    private Observable<? extends Collection<ConvertedCurrency>>
    getConvertedCurrencies() {

        return currencies
                .flatMap(convertCurrencyFunc());
    }

    private Func1<Collection<Currency>, Observable<? extends Collection<
            ConvertedCurrency>>>
    convertCurrencyFunc() {
        return new Func1<Collection<Currency>, Observable<?
                extends Collection<ConvertedCurrency>>>() {

            @Override
            public Observable<? extends Collection<ConvertedCurrency>> call
                    (final Collection<Currency> targetCurrencies) {

                return getSourceCurrency()
                        .flatMap(new Func1<Currency, Observable<? extends
                                Collection<ConvertedCurrency>>>() {

                            @Override
                            public Observable<? extends
                                    Collection<ConvertedCurrency>>
                            call(final Currency sourceCurrency) {

                                return sourceCurrencyValue()
                                        .map(convertCurrencyCollection
                                                (sourceCurrency,
                                                        targetCurrencies));
                            }
                        });
            }
        };
    }

    private Func1<BigDecimal, Collection<ConvertedCurrency>>
    convertCurrencyCollection(final Currency sourceCurrency,
                              final Collection<Currency> targetCurrencies) {
        return new Func1<BigDecimal,
                Collection<ConvertedCurrency>>() {

            @Override
            public Collection<ConvertedCurrency>
            call(final BigDecimal value) {
                return Collections2.transform
                        (targetCurrencies, new Function<Currency,
                                ConvertedCurrency>() {


                            @Override
                            public ConvertedCurrency
                            apply(final Currency
                                          targetCurrency) {
                                return
                                        ConvertedCurrency.convertFromTo
                                                (sourceCurrency,
                                                        targetCurrency, value);
                            }
                        });
            }
        };
    }

    @NotNull @Override
    public Observable<? extends Collection<ConvertedCurrency>>
    convertedCurrencies() {
        return getConvertedCurrencies();
    }

    @NotNull @Override public Observable<Currency> sourceCurrency() {
        return getSourceCurrency();
    }

    @NotNull
    @Override public ConverterPresenter attachToView(@NotNull final
                                                     ConverterView view) {
        attachView(checkNotNull(view));
        return this;
    }

    boolean isAttached() {
        return attachedToView;
    }

}
