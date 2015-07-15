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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.widget.EditText;

import io.fabric.sdk.android.services.common.CommonUtils;

import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.SessionManager;


class LoginCodeController extends DigitsControllerImpl {
    private final String requestId;
    private final long userId;
    private final String phoneNumber;

    LoginCodeController(ResultReceiver resultReceiver, StateButton stateButton,
                        EditText phoneEditText, String requestId, long userId, String phoneNumber) {
        this(resultReceiver, stateButton, phoneEditText, Digits.getSessionManager(),
                Digits.getInstance().getDigitsClient(), requestId, userId, phoneNumber,
                new ConfirmationErrorCodes(stateButton.getContext().getResources()),
                Digits.getInstance().getActivityClassManager());
    }

    LoginCodeController(ResultReceiver resultReceiver,
                        StateButton stateButton, EditText loginEditText,
                        SessionManager<DigitsSession> sessionManager, DigitsClient client,
                        String requestId, long userId, String phoneNumber, ErrorCodes errors,
                        ActivityClassManager activityClassManager) {
        super(resultReceiver, stateButton, loginEditText, client, errors, activityClassManager,
                sessionManager);
        this.requestId = requestId;
        this.userId = userId;
        this.phoneNumber = phoneNumber;
    }


    @Override
    public void executeRequest(final Context context) {
        if (validateInput(editText.getText())) {
            sendButton.showProgress();
            CommonUtils.hideKeyboard(context, editText);
            final String code = editText.getText().toString();
            digitsClient.loginDevice(requestId, userId, code,
                    new DigitsCallback<DigitsSessionResponse>(context, this) {
                        public void success(Result<DigitsSessionResponse> result) {
                            if (result.data.isEmpty()) {
                                startPinCodeActivity(context);
                            } else {
                                final DigitsSession session = DigitsSession.create(result.data,
                                        phoneNumber);
                                loginSuccess(context, session, phoneNumber);
                            }
                        }
                    });
        }
    }

    private void startPinCodeActivity(Context context) {
        final Intent intent = new Intent(context, activityClassManager.getPinCodeActivity());
        final Bundle bundle = getBundle(phoneNumber);
        bundle.putParcelable(DigitsClient.EXTRA_RESULT_RECEIVER, resultReceiver);
        bundle.putString(DigitsClient.EXTRA_REQUEST_ID, requestId);
        bundle.putLong(DigitsClient.EXTRA_USER_ID, userId);
        intent.putExtras(bundle);
        startActivityForResult((Activity) context, intent);
    }

    @Override
    Uri getTosUri() {
        return DigitsConstants.TWITTER_TOS;
    }
}
