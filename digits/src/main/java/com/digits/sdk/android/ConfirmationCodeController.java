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

class ConfirmationCodeController extends DigitsControllerImpl {
    private final String phoneNumber;


    ConfirmationCodeController(ResultReceiver resultReceiver, StateButton stateButton,
                               EditText phoneEditText, String phoneNumber) {
        this(resultReceiver, stateButton, phoneEditText, phoneNumber,
                Digits.getSessionManager(), Digits.getInstance().getDigitsClient(),
                new ConfirmationErrorCodes(stateButton.getContext().getResources()),
                Digits.getInstance().getActivityClassManager());
    }

    /**
     * Only for test
     */
    ConfirmationCodeController(ResultReceiver resultReceiver, StateButton stateButton,
                               EditText phoneEditText, String phoneNumber,
                               SessionManager<DigitsSession> sessionManager, DigitsClient client,
                               ErrorCodes errors, ActivityClassManager activityClassManager) {
        super(resultReceiver, stateButton, phoneEditText, client, errors, activityClassManager,
                sessionManager);
        this.phoneNumber = phoneNumber;
    }

    @Override
    public void executeRequest(final Context context) {
        if (validateInput(editText.getText())) {
            sendButton.showProgress();
            CommonUtils.hideKeyboard(context, editText);
            final String code = editText.getText().toString();
            digitsClient.createAccount(code, phoneNumber,
                    new DigitsCallback<DigitsUser>(context, this) {
                        @Override
                        public void success(Result<DigitsUser> result) {
                            final DigitsSession session = DigitsSession.create(result);
                            loginSuccess(context, session, phoneNumber);
                        }

                    });
        }
    }

    @Override
    Uri getTOSUri() {
        return DigitsConstants.TWITTER_TOS;
    }
}
