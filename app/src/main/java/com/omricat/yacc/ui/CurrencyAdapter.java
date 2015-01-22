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

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.omricat.yacc.R;
import com.omricat.yacc.model.Currency;
import com.omricat.yacc.model.CurrencyCode;

import org.jetbrains.annotations.NotNull;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Immutable(?) instance of a {@link android.support.v7.widget.RecyclerView
 * .Adapter}
 */
public class CurrencyAdapter extends RecyclerView.Adapter<CurrencyAdapter
        .ViewHolder> {

    private ImmutableList<Currency> cachedCurrencyList;
    private ImmutableSet<CurrencyCode> selectedCurrencies;

    public CurrencyAdapter(@NotNull final Iterable<Currency> currencyDataset,
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

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup viewGroup,
                                         final int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.cardview_currency, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder,
                                 final int i) {
        final Currency currency = cachedCurrencyList.get(i);
        viewHolder.vSelected.setChecked(selectedCurrencies.contains(currency.getCode()));
        viewHolder.vCode.setText(currency.getCode().getCode());
        viewHolder.vName.setText(currency.getName());
        viewHolder.vValue.setText(currency.getValueInUSD().toPlainString());
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
        @InjectView( R.id.selected)
        CheckBox vSelected;

        public ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

}
