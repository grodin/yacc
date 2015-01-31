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

package com.omricat.yacc.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.omricat.yacc.R;
import com.omricat.yacc.YaccApp;
import com.omricat.yacc.model.Currency;
import com.omricat.yacc.model.CurrencyCode;
import com.omricat.yacc.model.CurrencyDataset;
import com.omricat.yacc.rx.CurrencyCodeRxSet;
import com.omricat.yacc.rx.CurrencyDataRequester;
import com.omricat.yacc.rx.persistence.IsDataStalePredicate;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;
import rx.subscriptions.Subscriptions;

public class MainCurrencyFragment extends Fragment {

    @InjectView( R.id.cardRecyclerView )
    RecyclerView mCardRecyclerView;

    private CurrencyDataRequester currencyDataRequester;
    private CurrencyCodeRxSet selectedKeySet;

    private Observable<? extends Collection<Currency>> currenciesObs;
    private Observable<? extends Set<CurrencyCode>> selectedCurrenciesObs;

    private Subscription subscription = Subscriptions.empty();

    private CurrencyAdapter adapter;

    @Override public void onAttach(final Activity activity) {
        super.onAttach(activity);
        currencyDataRequester = YaccApp.from(activity)
                .getCurrencyDataRequester(IsDataStalePredicate.createDefault());

        selectedKeySet = YaccApp.from(activity).getCurrencyCodeRxSet();
    }

    @Override public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new CurrencyAdapter(Collections.<Currency>emptySet());

        selectedCurrenciesObs = RxUtils.bindFragmentOnIO(this,
                selectedKeySet.get());

        currenciesObs = RxUtils.bindFragmentOnIO(this,
                getCurrencyData());
    }


    private Observable<? extends Collection<Currency>> getCurrencyData() {
        return selectedCurrenciesObs.flatMap(new Func1<Set<CurrencyCode>,
                Observable<? extends Collection<Currency>>>() {


            @Override
            public Observable<? extends Collection<Currency>> call(final
                                                              Set<CurrencyCode> keys) {
                return currencyDataRequester.request()
                        .flatMap(filterCurrencyData(keys));
            }
        });
    }

    private Func1<CurrencyDataset, Observable<? extends Collection<Currency>>>
    filterCurrencyData(final Set<CurrencyCode> keys) {
        return new Func1<CurrencyDataset, Observable<? extends
                Collection<Currency>>>() {


            @Override
            public Observable<? extends Collection<Currency>>
            call(final CurrencyDataset currencyDataset) {
                return currencyDataset.asObservable()
                        .filter(new Func1<Currency, Boolean>() {
                            @Override
                            public Boolean call(final Currency currency) {
                                return keys.contains(currency.getCode());
                            }
                        }).toList();
            }
        };
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
        return rootView;
    }


    @Override public void onResume() {
        super.onResume();
        subscription = new CompositeSubscription(currenciesObs.subscribe(
                new Action1<Collection<Currency>>() {


                    @Override
                    public void call(final Collection<Currency> currencies) {
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
        subscription.unsubscribe();
        ButterKnife.reset(this);
        super.onDestroyView();
    }
}
