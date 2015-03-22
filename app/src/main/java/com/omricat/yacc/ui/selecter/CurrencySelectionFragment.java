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

package com.omricat.yacc.ui.selecter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.omricat.yacc.R;
import com.omricat.yacc.YaccApp;
import com.omricat.yacc.common.rx.RxSet;
import com.omricat.yacc.common.rx.RxSetOperation;
import com.omricat.yacc.data.model.Currency;
import com.omricat.yacc.data.model.CurrencyCode;
import com.omricat.yacc.data.model.SelectableCurrency;
import com.omricat.yacc.di.qualifiers.AllCurrencies;
import com.omricat.yacc.domain.OpToCurrencyCode;
import com.omricat.yacc.ui.rx.RxUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;
import rx.subscriptions.CompositeSubscription;
import rx.subscriptions.Subscriptions;

public class CurrencySelectionFragment extends Fragment implements
        SelecterView {

    // Debug Log tag
    private static final String TAG = CurrencySelectionFragment.class
            .getSimpleName();

    @InjectView( R.id.cardRecyclerView )
    RecyclerView mCardRecyclerView;


    @Inject @AllCurrencies
    Observable<? extends Collection<Currency>> allCurrencies;

    @Inject
    RxSet<CurrencyCode> selectedKeySet;

    private Observable<? extends Collection<Currency>> boundAllCurrencies;
    private Observable<? extends Collection<SelectableCurrency>>
            selectedCurrencies;

    private final BehaviorSubject<RxSetOperation<CurrencyCode>> getSubject =
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
        inject(activity);
    }

    private void inject(final Context context) {
        Dagger_SelecterComponent.builder()
                .yaccAppComponent(YaccApp.from(context).component())
                .selecterModule(new SelecterModule(this))
                .build().inject(this);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        selectableCurrencyAdapter = new SelectableCurrencyAdapter
                (Collections.<SelectableCurrency>emptyList());

        boundAllCurrencies = RxUtils.bindFragmentOnIO(this,
                allCurrencies);

        selectedCurrencies = RxUtils.bindFragmentOnIO(this,
                selectedKeySetObservable());
    }

    private Observable<? extends Collection<SelectableCurrency>>
    selectedKeySetObservable() {
        return Observable.merge(selectableCurrencyAdapter.selectionChanges(),
                getSubject)
                .flatMap(new OpToCurrencyCode(selectedKeySet))
                .flatMap(new Func1<Set<CurrencyCode>,
                        Observable<? extends Collection<SelectableCurrency>>>() {

                    @Override
                    public Observable<? extends
                            Collection<SelectableCurrency>> call(final
                                                                 Set<CurrencyCode>
                                                                         selectedKeys) {
                        return boundAllCurrencies
                                .flatMap(toSelectableCurrency(selectedKeys));
                    }
                });

    }

    private Func1<Collection<Currency>, Observable<? extends
            Collection<SelectableCurrency>>>
    toSelectableCurrency(final Set<CurrencyCode> selectedKeys) {
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
                                    selectedKeys.contains(currency.getCode()));
                        }
                    }).toList();
            }
        };
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
        getSubject.onNext(RxSetOperation.<CurrencyCode>get());
        subscription = new
                CompositeSubscription(
                selectedCurrencies
                        .subscribe(new Action1<Collection<SelectableCurrency>>() {
                            @Override public void call(final
                                                       Collection<SelectableCurrency> selectedCurrencies) {
                                selectableCurrencyAdapter
                                        .swapCurrencyList(selectedCurrencies);
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
