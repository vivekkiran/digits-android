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

import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.SessionManager;

import io.fabric.sdk.android.services.common.CommonUtils;

class PinCodeController extends DigitsControllerImpl {
    private final String requestId;
    private final long userId;
    private final String phoneNumber;
    private final Boolean isEmailCollection;

    PinCodeController(ResultReceiver resultReceiver, StateButton stateButton,
                      EditText phoneEditText, String requestId, long userId,
                      String phoneNumber, DigitsScribeService scribeService,
                      Boolean isEmailCollection) {
        this(resultReceiver, stateButton, phoneEditText, Digits.getSessionManager(),
                Digits.getInstance().getDigitsClient(), requestId, userId, phoneNumber,
                new ConfirmationErrorCodes(stateButton.getContext().getResources()),
                Digits.getInstance().getActivityClassManager(), scribeService, isEmailCollection);
    }

    PinCodeController(ResultReceiver resultReceiver, StateButton stateButton,
                      EditText phoneEditText, SessionManager<DigitsSession> sessionManager,
                      DigitsClient digitsClient, String requestId, long userId,
                      String phoneNumber, ErrorCodes errors,
                      ActivityClassManager activityClassManager,
                      DigitsScribeService scribeService, Boolean isEmailCollection) {
        super(resultReceiver, stateButton, phoneEditText, digitsClient, errors,
                activityClassManager, sessionManager, scribeService);
        this.requestId = requestId;
        this.userId = userId;
        this.phoneNumber = phoneNumber;
        this.isEmailCollection = isEmailCollection;
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
        scribeService.click(DigitsScribeConstants.Element.SUBMIT);
        if (validateInput(editText.getText())) {
            sendButton.showProgress();
            CommonUtils.hideKeyboard(context, editText);
            final String code = editText.getText().toString();
            digitsClient.verifyPin(requestId, userId, code,
                    new DigitsCallback<DigitsSessionResponse>(context, this) {
                        @Override
                        public void success(Result<DigitsSessionResponse> result) {
                            scribeService.success();
                            final DigitsSession session = DigitsSession.create(result.data,
                                    phoneNumber);
                            if (isEmailCollection) {
                                emailRequest(context, session);
                            } else {
                                loginSuccess(context, session, phoneNumber);
                            }
                        }
                    });
        }
    }

    private boolean canRequestEmail(DigitsSession newSession, DigitsSession session) {
        return isEmailCollection && newSession.getEmail().equals(DigitsSession.DEFAULT_EMAIL)
                && newSession.getId() == session.getId();
    }

    private void emailRequest(final Context context, final DigitsSession session) {
        getAccountService(session).verifyAccount
                (new DigitsCallback<VerifyAccountResponse>(context, this) {
                    @Override
                    public void success(Result<VerifyAccountResponse>
                                                result) {
                        final DigitsSession newSession =
                                DigitsSession.create(result.data);
                        if (canRequestEmail(newSession, session)) {
                            sessionManager.setActiveSession(newSession);
                            startEmailRequest(context, phoneNumber);
                        } else {
                            loginSuccess(context, newSession, phoneNumber);
                        }
                    }
                });
    }

    DigitsApiClient.AccountService getAccountService(DigitsSession session) {
        return new DigitsApiClient(session).getAccountService();
    }

}
