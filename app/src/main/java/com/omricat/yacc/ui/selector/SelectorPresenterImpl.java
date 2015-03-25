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

import com.omricat.yacc.common.rx.RxSet;
import com.omricat.yacc.data.model.Currency;
import com.omricat.yacc.data.model.CurrencyCode;
import com.omricat.yacc.data.model.SelectableCurrency;
import com.omricat.yacc.ui.events.ViewLifecycleEvent;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.omricat.yacc.ui.events.ViewLifecycleEvent.*;
import static com.omricat.yacc.ui.selector.CurrencySelectEvent.SelectEvent;
import static com.omricat.yacc.ui.selector.CurrencySelectEvent.UnselectEvent;

public class SelectorPresenterImpl implements SelectorPresenter {

    private final Observable<? extends Collection<Currency>> allCurrencies;

    private final RxSet<CurrencyCode> selectedKeySet;

    // Observables from SelectorView instance

    private Observable<? extends CurrencySelectEvent> selectEvents
            = Observable.empty();

    // Subscription

    private Subscription lifecycleSubscription;

    public SelectorPresenterImpl(@NotNull final
                                 Observable<? extends Collection<Currency>>
                                         allCurrencies,
                                 @NotNull final RxSet<CurrencyCode>
                                         selectedKeySet) {

        this.allCurrencies = checkNotNull(allCurrencies);
        this.selectedKeySet = checkNotNull(selectedKeySet);
    }

    private final ViewLifecycleEvent.Matcher matcher =
            new ViewLifecycleEvent.Matcher() {
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

    private void detachView() {
        lifecycleSubscription.unsubscribe();
        selectEvents = Observable.empty();
    }

    private void attachView(final SelectorView v) {
        selectEvents = v.selectionChangeEvents()
                .debounce(300, TimeUnit.MILLISECONDS);

        lifecycleSubscription = v.lifeCycleEvents()
                .subscribe(new Action1<ViewLifecycleEvent>() {


                    @Override
                    public void call(final ViewLifecycleEvent e) {
                        e.match(matcher);
                    }
                });
    }

    @NotNull @Override
    public SelectorPresenter attachToView(@NotNull final SelectorView
                                                  selectorView) {
        attachView(checkNotNull(selectorView));

        return this;
    }


    private Func1<Collection<Currency>, Observable<? extends
            Collection<SelectableCurrency>>>
    toSelectableCurrency(final Collection<CurrencyCode> selectedKeys) {
        return new Func1<Collection<Currency>,
                Observable<? extends Collection<SelectableCurrency>>>() {

            @Override
            public Observable<? extends
                    Collection<SelectableCurrency>> call
                    (final Collection<Currency> currencies) {
                return Observable.from(currencies)
                        .map(new Func1<Currency,
                                SelectableCurrency>() {


                            @Override
                            public SelectableCurrency
                            call(final Currency currency) {
                                return SelectableCurrency.select(currency,
                                        selectedKeys.contains(currency
                                                .getCode()));
                            }
                        }).toList();
            }
        };
    }

    private Observable<? extends Collection<SelectableCurrency>>
    getSelectableCurrencies() {
        return selectEvents.flatMap(new Func1<CurrencySelectEvent,
                Observable<? extends Collection<CurrencyCode>>>() {

            @Override
            public Observable<? extends Collection<CurrencyCode>>
            call(final CurrencySelectEvent event) {
                return event.match(
                        new CurrencySelectEvent.Matcher<Observable<? extends
                                Collection<CurrencyCode>>>() {

                            @Override
                            public Observable<? extends
                                    Collection<CurrencyCode>>
                            matchSelectEvent(final SelectEvent e) {
                                return selectedKeySet.add(e.code());
                            }

                            @Override
                            public Observable<? extends
                                    Collection<CurrencyCode>>
                            matchUnselectEvent(final UnselectEvent e) {
                                return selectedKeySet.remove(e.code());
                            }
                        });
            }
        })
                .startWith(getSelectedKeys())
                .flatMap(new Func1<Collection<CurrencyCode>, Observable<? extends Collection<SelectableCurrency>>>() {


                    @Override
                    public Observable<? extends
                            Collection<SelectableCurrency>>
                    call(final Collection<CurrencyCode> currencyCodes) {
                        return allCurrencies
                                .flatMap(toSelectableCurrency(currencyCodes));
                    }
                });
    }

    /*
     * This method is purely because Java's type inference cannot tell that
     * Observable<? extends Collection<T>> really _should_ be a supertype of
     * Observable<? extends Set<T>>
     */
    private Observable<Collection<CurrencyCode>> getSelectedKeys() {
        return selectedKeySet.get().map(new Func1<Set<CurrencyCode>,
                Collection<CurrencyCode>>() {


            @Override
            public Collection<CurrencyCode> call(final Set<CurrencyCode>
                                                         currencyCodes) {
                return currencyCodes;
            }
        });
    }

    @NotNull @Override
    public Observable<? extends Collection<SelectableCurrency>>
    selectableCurrencies() {
        return getSelectableCurrencies();
    }
}
