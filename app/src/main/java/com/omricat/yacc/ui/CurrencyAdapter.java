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
import android.widget.TextView;

import com.omricat.yacc.R;
import com.omricat.yacc.data.Currencies;
import com.omricat.yacc.data.Currency;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Immutable(?) instance of a {@link android.support.v7.widget.RecyclerView.Adapter}
 */
public class CurrencyAdapter extends RecyclerView.Adapter<CurrencyAdapter
        .CurrencyViewHolder> {

    private Currencies currencies;
    private List<Currency> cachedCurrencyList;

    public CurrencyAdapter(@NotNull final Currencies currencies) {
        swapCurrencies(currencies);
    }

    public void swapCurrencies(@NotNull final Currencies currencies) {
        this.currencies = checkNotNull(currencies);
        cachedCurrencyList = Arrays.asList(currencies.getCurrencies().toArray
                (new Currency[]{}));
        notifyDataSetChanged();
    }

    @Override
    public CurrencyViewHolder onCreateViewHolder(final ViewGroup viewGroup,
                                                 final int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.cardview_currency, viewGroup, false);
        return new CurrencyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final CurrencyViewHolder currencyViewHolder,
                                 final int i) {
        final Currency currency = cachedCurrencyList.get(i);
        currencyViewHolder.vCode.setText(currency.getCode());
        currencyViewHolder.vName.setText(currency.getName());
        currencyViewHolder.vValue.setText(currency.getValueInUSD().toPlainString());
    }

    @Override
    public int getItemCount() {
        return cachedCurrencyList.size();
    }

    public static class CurrencyViewHolder extends RecyclerView.ViewHolder {

        @InjectView( R.id.code )
        TextView vCode;
        @InjectView( R.id.name )
        TextView vName;
        @InjectView( R.id.value )
        TextView vValue;

        public CurrencyViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

}
