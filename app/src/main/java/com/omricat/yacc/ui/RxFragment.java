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

import rx.Observable;
import rx.android.observables.AndroidObservable;
import rx.subjects.PublishSubject;

public abstract class RxFragment extends Fragment {

    protected final PublishSubject<Void> detached = PublishSubject.create();

    @Override
    public void onDetach() {
        super.onDetach();
        detached.onNext(null);
    }


    public final <T> Observable<T> bindObservable(Observable<T> in) {
        return AndroidObservable.bindFragment(this, in).takeUntil(detached);
    }
}
