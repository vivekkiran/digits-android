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

import android.os.Bundle;
import android.os.ResultReceiver;

import com.twitter.sdk.android.core.SessionManager;


class LoginResultReceiver extends ResultReceiver {

    static final int RESULT_OK = 200;
    static final int RESULT_ERROR = 400;
    static final String KEY_ERROR = "login_error";

    final WeakAuthCallback callback;
    final SessionManager<DigitsSession> sessionManager;

    LoginResultReceiver(AuthCallback callback, SessionManager<DigitsSession> sessionManager) {
        super(null);
        this.callback = new WeakAuthCallback(callback);
        this.sessionManager = sessionManager;
    }
    //Test only
    LoginResultReceiver(WeakAuthCallback callback, SessionManager<DigitsSession> sessionManager) {
        super(null);
        this.callback = callback;
        this.sessionManager = sessionManager;
    }


    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        if (callback != null) {
            if (resultCode == RESULT_OK) {
                callback.success(sessionManager.getActiveSession(),
                        resultData.getString(DigitsClient.EXTRA_PHONE));
            } else if (resultCode == RESULT_ERROR) {
                callback.failure(new DigitsException(resultData.getString(KEY_ERROR)));
            }
        }
    }
}
