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

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.omricat.yacc.R;
import com.omricat.yacc.data.model.ConvertedCurrency;
import com.omricat.yacc.data.model.Currency;
import com.omricat.yacc.debug.InMemoryPersister;
import com.omricat.yacc.ui.converter.events.ChooseCurrencyEvent;
import com.omricat.yacc.ui.converter.events.ConverterViewLifecycleEvent;
import com.omricat.yacc.ui.converter.events.CurrencyValueChangeEvent;
import com.omricat.yacc.ui.rx.RxUtils;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import rx.Subscription;
import rx.android.events.OnTextChangeEvent;
import rx.android.observables.ViewObservable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;
import rx.subscriptions.Subscriptions;

public class MainCurrencyFragment extends Fragment implements ConverterView {

    @InjectView(R.id.cardRecyclerView)
    RecyclerView mCardRecyclerView;

    @InjectView(R.id.user_value)
    EditText vUserValue;

    private ConverterPresenter presenter;

    private Observable<? extends Collection<ConvertedCurrency>> currencies;
    private Observable<CurrencyValueChangeEvent> userValueObs;

    private PublishSubject<ConverterViewLifecycleEvent> lifeCycleEvents =
            PublishSubject.create();

    private Subscription subscription = Subscriptions.empty();

    private ConvertedCurrencyAdapter adapter;

    private void inject() {
        presenter = ConverterPresenter.Factory.create(this,
                new InMemoryPersister<Currency>());
    }

    @Override public void onAttach(final Activity activity) {
        super.onAttach(activity);

        inject();

        lifeCycleEvents.onNext(ConverterViewLifecycleEvent.onAttach(activity));
    }

    @Override public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new ConvertedCurrencyAdapter(Collections
                .<ConvertedCurrency>emptySet());

        currencies = RxUtils.bindFragmentOnIO(this,
                presenter.convertedCurrencies());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_currencies,
                container, false);
        setRetainInstance(true);
        ButterKnife.inject(this, rootView);

        mCardRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mCardRecyclerView.setLayoutManager(llm);
        mCardRecyclerView.setAdapter(adapter);

        userValueObs = ViewObservable.text(vUserValue)
            .map(new Func1<OnTextChangeEvent, CurrencyValueChangeEvent>() {



                @Override
                public CurrencyValueChangeEvent call(final OnTextChangeEvent
                                                             e) {
                    return CurrencyValueChangeEvent.of(new BigDecimal(e.text
                            .toString()));
                }
            });

        return rootView;
    }


    @Override public void onResume() {
        super.onResume();

        lifeCycleEvents.onNext(ConverterViewLifecycleEvent.onResume());

        subscription = new CompositeSubscription(currencies.subscribe(
                new Action1<Collection<ConvertedCurrency>>() {


                    @Override
                    public void call(final Collection<ConvertedCurrency> currencies) {
                        adapter.swapCurrencyData(currencies);
                    }
                }, new Action1<Throwable>() {
                    @Override public void call(final Throwable throwable) {
                        throw new RuntimeException(throwable);
                    }
                }));
    }

    @Override
    public void onDestroyView() {

        lifeCycleEvents.onNext(ConverterViewLifecycleEvent.onDestroy());

        subscription.unsubscribe();
        ButterKnife.reset(this);

        super.onDestroyView();
    }

    @Override public Observable<ChooseCurrencyEvent> chooseCurrencyEvents() {
        return null;
    }

    @Override
    public Observable<CurrencyValueChangeEvent> convertFromValueChangeEvents() {
        return userValueObs;
    }

    @Override public Observable<ConverterViewLifecycleEvent> lifecycleEvents() {
        return lifeCycleEvents;
    }
}
