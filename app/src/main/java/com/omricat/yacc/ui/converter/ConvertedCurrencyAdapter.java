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

package com.omricat.yacc.ui.converter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.common.collect.ImmutableList;
import com.omricat.yacc.R;
import com.omricat.yacc.data.model.ConvertedCurrency;

import org.jetbrains.annotations.NotNull;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.google.common.base.Preconditions.checkNotNull;

public class ConvertedCurrencyAdapter extends
        RecyclerView.Adapter<ConvertedCurrencyAdapter.ViewHolder> {

    private ImmutableList<ConvertedCurrency> cachedCurrencyList;

    public ConvertedCurrencyAdapter(@NotNull final Iterable<ConvertedCurrency>
                                            currencies) {

        setCurrencyList(checkNotNull(currencies));
    }

    public void swapCurrencyData(@NotNull final Iterable<ConvertedCurrency>
                                         currencies) {
        setCurrencyList(checkNotNull(currencies));
    }

    private void setCurrencyList(final Iterable<ConvertedCurrency>
                                         currencyList) {
        this.cachedCurrencyList = ImmutableList.copyOf(currencyList);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int
            viewType) {
        final View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_currency, parent, false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final ConvertedCurrency currency = cachedCurrencyList.get(position);
        holder.vValue.setText(currency.getConvertedValue().toPlainString());
        holder.vCode.setText(currency.getCode().toString());
        holder.vName.setText(currency.getName());
        holder.vRate.setText(currency.getConversionRate().toPlainString());
    }

    @Override public int getItemCount() {
        return cachedCurrencyList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @InjectView( R.id.value )
        TextView vValue;
        @InjectView( R.id.code )
        TextView vCode;
        @InjectView( R.id.name )
        TextView vName;
        @InjectView( R.id.rate )
        TextView vRate;

        public ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }


}
