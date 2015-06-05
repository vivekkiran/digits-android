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

import android.content.Context;
import android.net.Uri;
import android.os.ResultReceiver;
import android.widget.EditText;

import io.fabric.sdk.android.services.common.CommonUtils;

import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.SessionManager;

class PinCodeController extends DigitsControllerImpl {
    private final String requestId;
    private final long userId;
    private final String phoneNumber;

    PinCodeController(ResultReceiver resultReceiver, StateButton stateButton,
                      EditText phoneEditText, String requestId, long userId,
                      String phoneNumber) {
        this(resultReceiver, stateButton, phoneEditText, Digits.getSessionManager(),
                Digits.getInstance().getDigitsClient(), requestId, userId, phoneNumber,
                new ConfirmationErrorCodes(stateButton.getContext().getResources()),
                Digits.getInstance().getActivityClassManager());
    }

    PinCodeController(ResultReceiver resultReceiver, StateButton stateButton,
                      EditText phoneEditText, SessionManager<DigitsSession>
            sessionManager, DigitsClient digitsClient, String requestId, long userId,
                      String phoneNumber, ErrorCodes errors,
                      ActivityClassManager activityClassManager) {
        super(resultReceiver, stateButton, phoneEditText, digitsClient, errors,
                activityClassManager, sessionManager);
        this.requestId = requestId;
        this.userId = userId;
        this.phoneNumber = phoneNumber;
    }

    @Override
    public void showTOS(Context context) {
        //nothing to do
    }

    @Override
    Uri getTosUri() {
        return null;
    }

    @Override
    public void executeRequest(final Context context) {
        if (validateInput(editText.getText())) {
            sendButton.showProgress();
            CommonUtils.hideKeyboard(context, editText);
            final String code = editText.getText().toString();
            digitsClient.verifyPin(requestId, userId, code,
                    new DigitsCallback<DigitsSessionResponse>(context, this) {
                        @Override
                        public void success(Result<DigitsSessionResponse> result) {
                            final DigitsSession session = DigitsSession.create(result.data);
                            loginSuccess(context, session, phoneNumber);
                        }
                    });
        }
    }
}
