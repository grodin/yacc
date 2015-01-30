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
import com.google.common.collect.ImmutableSet;
import com.omricat.yacc.R;
import com.omricat.yacc.model.Currency;
import com.omricat.yacc.model.CurrencyCode;
import com.omricat.yacc.rx.persistence.Operation;

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

    private ImmutableList<Currency> cachedCurrencyList;
    private ImmutableSet<CurrencyCode> selectedCurrencies;
    private final PublishSubject<Operation<CurrencyCode>> publishSubject =
            PublishSubject.create();

    public SelectableCurrencyAdapter(@NotNull final Iterable<Currency>
                                             currencyDataset,
                                     @NotNull final Iterable<CurrencyCode>
                                             selectedCurrencies) {
        swapSelected(selectedCurrencies);
        swapCurrencies(currencyDataset);
        notifyDataSetChanged();
    }

    public void swapCurrencyList(@NotNull final Iterable<Currency>
                                         currencyDataset) {
        swapCurrencies(currencyDataset);
        notifyDataSetChanged();
    }

    private void swapCurrencies(final Iterable<Currency> currencyDataset) {
        cachedCurrencyList = ImmutableList.copyOf(
                checkNotNull(currencyDataset));
    }

    public void swapSelectedCurrencies(@NotNull final Iterable<CurrencyCode>
                                               selectedCurrencies) {
        swapSelected(selectedCurrencies);
        notifyDataSetChanged();
    }

    private void swapSelected(final Iterable<CurrencyCode> selectedCurrencies) {
        this.selectedCurrencies = ImmutableSet.copyOf(
                checkNotNull(selectedCurrencies));
    }

    @NotNull
    public Observable<Operation<CurrencyCode>> selectionChanges() {
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
        final Currency currency = cachedCurrencyList.get(i);
        final CurrencyCode code = currency.getCode();
        final boolean selected = selectedCurrencies.contains(code);
        holder.vSelected.setChecked(selected);
        holder.vSelected.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(final View v) {
                if (((Checkable) v).isChecked()) {
                    publishSubject.onNext(Operation.add(code));
                } else {
                    publishSubject.onNext(Operation.remove(code));
                }
            }
        });

        holder.vCode.setText(code.toString());

        holder.vCode.setTextColor(selected ? Color.GREEN: Color.LTGRAY);

        holder.vName.setText(currency.getName());
        holder.vValue.setText(currency.getValueInUSD().toPlainString());
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
        @InjectView( R.id.value )
        TextView vValue;
        @InjectView( R.id.selected )
        CheckBox vSelected;

        public ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

}
