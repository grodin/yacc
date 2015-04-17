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

package com.omricat.yacc.ui.selector;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.omricat.yacc.R;
import com.omricat.yacc.YaccApp;
import com.omricat.yacc.common.rx.RxSetOperation;
import com.omricat.yacc.data.model.CurrencyCode;
import com.omricat.yacc.data.model.SelectableCurrency;
import com.omricat.yacc.ui.events.ViewLifecycleEvent;
import com.omricat.yacc.ui.rx.RxUtils;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;
import rx.subscriptions.Subscriptions;

public class CurrencySelectionFragment extends Fragment implements
        SelectorView {

    // Debug Log tag
    private static final String TAG = CurrencySelectionFragment.class
            .getSimpleName();

    @InjectView( R.id.cardRecyclerView )
    RecyclerView mCardRecyclerView;

    @Inject
    SelectorPresenter presenter;

    // From presenter

    private Observable<? extends Collection<SelectableCurrency>>
            selectedCurrencies;

    // To presenter

    private final PublishSubject<ViewLifecycleEvent> lifecycleEvents =
            PublishSubject.create();

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

    @Override public void onAttach(final Activity activity) {
        super.onAttach(activity);
        inject(activity);
    }

    private void inject(final Context context) {
        DaggerSelectorComponent.builder()
                .yaccAppComponent(YaccApp.from(context).component())
                .build().inject(this);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        selectableCurrencyAdapter = new SelectableCurrencyAdapter
                (Collections.<SelectableCurrency>emptyList());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_currencies_selection,
                container, false);
        setRetainInstance(true);
        return rootView;
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle
            savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.inject(this, view);

        mCardRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mCardRecyclerView.setLayoutManager(llm);
        mCardRecyclerView.setAdapter(selectableCurrencyAdapter);

        presenter.attachToView(this);

        selectedCurrencies = RxUtils.bindFragmentOnIO(this,
                presenter.selectableCurrencies());
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

        lifecycleEvents.onNext(ViewLifecycleEvent.onDestroy());
        subscription.unsubscribe();
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @NotNull @Override
    public Observable<ViewLifecycleEvent> lifeCycleEvents() {
        return lifecycleEvents.asObservable();
    }

    @NotNull @Override
    public Observable<CurrencySelectEvent>
    selectionChangeEvents() {
        return selectableCurrencyAdapter.selectionChanges();
    }
}
