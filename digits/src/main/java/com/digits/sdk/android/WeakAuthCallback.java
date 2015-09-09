/*
 * Copyright (C) 2015 Twitter, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.digits.sdk.android;

import java.lang.ref.WeakReference;

class WeakAuthCallback implements AuthCallback {
    private final WeakReference<AuthCallback> callbackWeakReference;
    private final DigitsScribeService scribeService;

    public WeakAuthCallback(AuthCallback callback) {
        this(callback, new AuthScribeService(Digits.getInstance().getScribeClient()));
    }

    WeakAuthCallback(AuthCallback callback,
                     DigitsScribeService scribeService) {
        this.callbackWeakReference = new WeakReference<>(callback);
        this.scribeService = scribeService;
    }

    @Override
    public void success(DigitsSession session, String phoneNumber) {
        final AuthCallback callback = callbackWeakReference.get();
        if (callback != null) {
            scribeService.success();
            callback.success(session, phoneNumber);
        }
    }

    @Override
    public void failure(DigitsException error) {
        final AuthCallback callback = callbackWeakReference.get();
        if (callback != null) {
            scribeService.failure();
            callback.failure(error);
        }
    }

    public AuthCallback getCallback() {
        return callbackWeakReference.get();
    }
}
