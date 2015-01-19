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
import com.omricat.yacc.api.NetworkCurrenciesService;
import com.omricat.yacc.model.CurrencyDataset;
import com.omricat.yacc.model.CurrencyKey;

import java.util.Set;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.RestAdapter;
import retrofit.converter.JacksonConverter;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.subscriptions.Subscriptions;

/**
 * A placeholder fragment containing a simple view.
 */
public class CurrencyListFragment extends Fragment {

    // Debug Log tag
    private static final String TAG = "CurrencyListFragment";

    @InjectView( R.id.cardRecyclerView )
    RecyclerView mCardRecyclerView;

    private Observable<CurrencyDataset> allCurrencies;
    private Observable<Set<CurrencyKey>> selectedCurrencies;
    private Subscription subscription = Subscriptions.empty();

    private NetworkCurrenciesService service;

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
        final RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://hp:8080")
                .setConverter(new JacksonConverter())
                .build();
        service = new NetworkCurrenciesService(restAdapter.create
                (CurrenciesService.class));
        currencyAdapter = new CurrencyAdapter(CurrencyDataset.EMPTY);


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
        subscription = allCurrencies.subscribe(new Subscriber<CurrencyDataset>
                () {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(final Throwable e) {
                        e.printStackTrace();
                        throw new IllegalStateException(e);
                    }

                    @Override
                    public void onNext(final CurrencyDataset currencyDataset) {
                                currencyAdapter.swapCurrencies
                                        (currencyDataset);
                        }
                });
    }

    @Override
    public void onDestroyView() {
        subscription.unsubscribe();
        ButterKnife.reset(this);
        super.onDestroyView();
    }
}
