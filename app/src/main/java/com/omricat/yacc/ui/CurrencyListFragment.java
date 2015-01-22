/*
 * Copyright 2014 Omricat Software
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

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.omricat.yacc.R;
import com.omricat.yacc.api.CurrenciesService;
import com.omricat.yacc.debug.DebugCurrenciesService;
import com.omricat.yacc.debug.TestPersister;
import com.omricat.yacc.model.CurrencyDataset;
import com.omricat.yacc.model.CurrencyKey;
import com.omricat.yacc.rx.CurrencyDataRequester;
import com.omricat.yacc.rx.CurrencyKeyRxSet;
import com.omricat.yacc.rx.persistence.IsDataStalePredicate;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;
import rx.subscriptions.Subscriptions;

import static rx.android.observables.AndroidObservable.bindFragment;

public class CurrencyListFragment extends Fragment {

    // Debug Log tag
    private static final String TAG = "CurrencyListFragment";

    @InjectView( R.id.cardRecyclerView )
    RecyclerView mCardRecyclerView;

    private CurrencyDataRequester currencyDataRequester;
    private CurrencyKeyRxSet selectedKeySet;

    private Observable<CurrencyDataset> allCurrencies;
    private Observable<? extends Set<CurrencyKey>> selectedCurrencies;
    private Subscription subscription = Subscriptions.empty();

    private CurrencyAdapter currencyAdapter;

    /**
     * Returns a new instance of this fragment for the given section number.
     */
    public static CurrencyListFragment newInstance() {
        return new CurrencyListFragment();
    }

    public CurrencyListFragment() {
        setRetainInstance(true);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final CurrenciesService service;
        try {
            service = new DebugCurrenciesService();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        currencyAdapter = new CurrencyAdapter(CurrencyDataset.EMPTY
                .getCurrencies(), Collections.<CurrencyKey>emptySet());

        currencyDataRequester = CurrencyDataRequester.create(
                new TestPersister<CurrencyDataset>(),
                service,
                IsDataStalePredicate.createDefault());

        selectedKeySet = CurrencyKeyRxSet.create(
                new TestPersister<Set<CurrencyKey>>());

        allCurrencies = bindFragment(this, currencyDataRequester.request());

        selectedCurrencies = bindFragment(this, selectedKeySet.get());
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
        mCardRecyclerView.setAdapter(currencyAdapter);
        return rootView;
    }

    @Override
    public void onViewCreated(final View view,
                              final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        subscription = new CompositeSubscription(
                allCurrencies.subscribe(new Subscriber<CurrencyDataset>
                () {
                    @Override
                    public void onCompleted() { }

                    @Override
                    public void onError(final Throwable e) {
                        throw new RuntimeException(e);
                    }

                    @Override
                    public void onNext(final CurrencyDataset currencyDataset) {
                                currencyAdapter.swapCurrencyList
                                        (currencyDataset.getCurrencies());
                        }
                }),
                selectedCurrencies.subscribe(new Subscriber<Set<CurrencyKey>>() {


                    @Override public void onCompleted() { }

                    @Override public void onError(final Throwable e) {
                        throw new RuntimeException(e);
                    }

                    @Override public void onNext(final Set<CurrencyKey> keys) {
                                currencyAdapter.swapSelectedCurrencies(keys);
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
