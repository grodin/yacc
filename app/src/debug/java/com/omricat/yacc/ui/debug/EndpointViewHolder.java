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

package com.omricat.yacc.ui.debug;

import android.view.View;
import android.widget.TextView;

import com.omricat.yacc.data.network.NetworkEndpoint;

import uk.co.ribot.easyadapter.ItemViewHolder;
import uk.co.ribot.easyadapter.PositionInfo;
import uk.co.ribot.easyadapter.annotations.LayoutId;
import uk.co.ribot.easyadapter.annotations.ViewId;

@LayoutId( android.R.layout.simple_spinner_dropdown_item )
public class EndpointViewHolder extends ItemViewHolder<NetworkEndpoint> {

    @ViewId( android.R.id.text1 )
    TextView textView;

    public EndpointViewHolder(final View view) {
        super(view);
    }

    public EndpointViewHolder(final View view, final NetworkEndpoint item) {
        super(view, item);
    }

    @Override
    public void onSetValues(final NetworkEndpoint networkEndpoint, final PositionInfo positionInfo) {
        textView.setText(networkEndpoint.getEndpoint().getName());
    }
}
