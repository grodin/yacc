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

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.omricat.yacc.R;
import com.omricat.yacc.YaccApp;
import com.omricat.yacc.model.CurrencyCode;
import com.omricat.yacc.model.CurrencyDataset;
import com.omricat.yacc.rx.CurrencyCodeRxSet;
import com.omricat.yacc.rx.CurrencyDataRequester;
import com.omricat.yacc.rx.persistence.IsDataStalePredicate;
import com.omricat.yacc.rx.persistence.OpToCurrencyCode;
import com.omricat.yacc.rx.persistence.Operation;

import java.util.Collections;
import java.util.Set;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.BehaviorSubject;
import rx.subscriptions.CompositeSubscription;
import rx.subscriptions.Subscriptions;

public class CurrencySelectionFragment extends Fragment {

    // Debug Log tag
    private static final String TAG = CurrencySelectionFragment.class
            .getSimpleName();

    @InjectView( R.id.cardRecyclerView )
    RecyclerView mCardRecyclerView;

    private CurrencyDataRequester currencyDataRequester;
    private CurrencyCodeRxSet selectedKeySet;

    private Observable<CurrencyDataset> allCurrencies;
    private Observable<? extends Set<CurrencyCode>> selectedCurrencies;
    private final BehaviorSubject<Operation<CurrencyCode>> getSubject =
            BehaviorSubject.create();

    private Subscription subscription = Subscriptions.empty();

    private SelectableCurrencyAdapter selectableCurrencyAdapter;

    /**
     * Returns a new instance of this fragment.
     */
    public static CurrencySelectionFragment newInstance() {
        return new CurrencySelectionFragment();
    }

    public CurrencySelectionFragment() {
        setRetainInstance(true);
    }

    @Override public void onAttach(final Activity activity) {
        super.onAttach(activity);
        currencyDataRequester = YaccApp.from(activity)
                .getCurrencyDataRequester(IsDataStalePredicate.createDefault());

        selectedKeySet = YaccApp.from(activity).getCurrencyCodeRxSet();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        selectableCurrencyAdapter = new SelectableCurrencyAdapter
                (CurrencyDataset.EMPTY
                .getCurrencies(), Collections.<CurrencyCode>emptySet());

        allCurrencies = RxUtils.bindFragmentOnIO(this,
                currencyDataRequester.request());

        selectedCurrencies = RxUtils.bindFragmentOnIO(this,
                selectedKeySetObservable());
    }

    private Observable<? extends Set<CurrencyCode>> selectedKeySetObservable() {
        return Observable.merge(selectableCurrencyAdapter.selectionChanges(),
                getSubject)
                .flatMap(new OpToCurrencyCode(selectedKeySet));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_currencies_selection,
                container, false);
        setRetainInstance(true);
        ButterKnife.inject(this, rootView);

        mCardRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mCardRecyclerView.setLayoutManager(llm);
        mCardRecyclerView.setAdapter(selectableCurrencyAdapter);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getSubject.onNext(Operation.<CurrencyCode>get());
        subscription = new
                CompositeSubscription(
                allCurrencies.subscribe(new Action1<CurrencyDataset>() {
                    @Override
                    public void call(final CurrencyDataset currencyDataset) {
                        selectableCurrencyAdapter.swapCurrencyList
                                (currencyDataset.getCurrencies());

                    }
                }, new Action1<Throwable>() {
                    @Override public void call(final Throwable throwable) {
                        throw new RuntimeException(throwable);
                    }
                }),
                selectedCurrencies
                        .subscribe(new Action1<Set<CurrencyCode>>() {
                            @Override public void call(final
                                                       Set<CurrencyCode> keys) {
                                selectableCurrencyAdapter
                                        .swapSelectedCurrencies(keys);
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(final Throwable throwable) {
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
