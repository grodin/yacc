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

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.TextView;

import com.google.common.collect.ImmutableList;
import com.omricat.yacc.R;
import com.omricat.yacc.common.rx.RxSetOperation;
import com.omricat.yacc.data.model.CurrencyCode;
import com.omricat.yacc.data.model.SelectableCurrency;

import org.jetbrains.annotations.NotNull;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import rx.subjects.PublishSubject;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Mutable instance of a {@link android.support.v7.widget.RecyclerView
 * .Adapter}
 */
public class SelectableCurrencyAdapter extends RecyclerView.Adapter<SelectableCurrencyAdapter
        .ViewHolder> {

    private ImmutableList<SelectableCurrency> cachedCurrencyList;
    private final PublishSubject<RxSetOperation<CurrencyCode>> publishSubject =
            PublishSubject.create();

    public SelectableCurrencyAdapter(@NotNull final Iterable<SelectableCurrency>
                                             selectedCurrencies) {
        swapCurrencies(selectedCurrencies);
        notifyDataSetChanged();
    }

    public void swapCurrencyList(@NotNull final Iterable<SelectableCurrency>
                                         selectableCurrencies) {
        swapCurrencies(checkNotNull(selectableCurrencies));
        notifyDataSetChanged();
    }

    private void swapCurrencies(final Iterable<SelectableCurrency>
                                        currencyDataset) {
        cachedCurrencyList = ImmutableList.copyOf(currencyDataset);
    }

    @NotNull
    public Observable<RxSetOperation<CurrencyCode>> selectionChanges() {
        return publishSubject.asObservable();
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup viewGroup,
                                         final int i) {
        final View v = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.cardview_currency_selectable, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder,
                                 final int i) {
        final SelectableCurrency currency = cachedCurrencyList.get(i);
        final CurrencyCode code = currency.getCode();
        final boolean selected = currency.isSelected();
        holder.vSelected.setChecked(selected);
        holder.vSelected.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(final View v) {
                if (((Checkable) v).isChecked()) {
                    publishSubject.onNext(RxSetOperation.add(code));
                } else {
                    publishSubject.onNext(RxSetOperation.remove(code));
                }
            }
        });

        holder.vCode.setText(code.toString());

        holder.vCode.setTextColor(selected ? Color.GREEN: Color.LTGRAY);

        holder.vName.setText(currency.getName());
        holder.vValue.setText(currency.getRateInUSD().toPlainString());
    }

    @Override
    public int getItemCount() {
        return cachedCurrencyList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @InjectView( R.id.code )
        TextView vCode;
        @InjectView( R.id.name )
        TextView vName;
        @InjectView( R.id.rate)
        TextView vValue;
        @InjectView( R.id.selected )
        CheckBox vSelected;

        public ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

}
