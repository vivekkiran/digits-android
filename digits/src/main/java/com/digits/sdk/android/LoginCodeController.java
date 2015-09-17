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

import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.SessionManager;

import io.fabric.sdk.android.services.common.CommonUtils;


class LoginCodeController extends DigitsControllerImpl {
    private final String requestId;
    private final long userId;
    private final String phoneNumber;
    private final Boolean emailCollection;

    LoginCodeController(ResultReceiver resultReceiver, StateButton stateButton,
                        EditText phoneEditText, String requestId, long userId, String
                                phoneNumber, DigitsScribeService scribeService,
                        Boolean emailCollection) {
        this(resultReceiver, stateButton, phoneEditText, Digits.getSessionManager(),
                Digits.getInstance().getDigitsClient(), requestId, userId, phoneNumber,
                new ConfirmationErrorCodes(stateButton.getContext().getResources()),
                Digits.getInstance().getActivityClassManager(), scribeService,
                emailCollection);
    }

    LoginCodeController(ResultReceiver resultReceiver,
                        StateButton stateButton, EditText loginEditText,
                        SessionManager<DigitsSession> sessionManager, DigitsClient client,
                        String requestId, long userId, String phoneNumber, ErrorCodes errors,
                        ActivityClassManager activityClassManager,
                        DigitsScribeService scribeService, Boolean emailCollection) {
        super(resultReceiver, stateButton, loginEditText, client, errors, activityClassManager,
                sessionManager, scribeService);
        this.requestId = requestId;
        this.userId = userId;
        this.phoneNumber = phoneNumber;
        this.emailCollection = emailCollection;
    }


    @Override
    public void executeRequest(final Context context) {
        scribeService.click(DigitsScribeConstants.Element.SUBMIT);
        if (validateInput(editText.getText())) {
            sendButton.showProgress();
            CommonUtils.hideKeyboard(context, editText);
            final String code = editText.getText().toString();
            digitsClient.loginDevice(requestId, userId, code,
                    new DigitsCallback<DigitsSessionResponse>(context, this) {
                        public void success(Result<DigitsSessionResponse> result) {
                            scribeService.success();
                            if (result.data.isEmpty()) {
                                startPinCodeActivity(context);
                            } else if (emailCollection) {
                                final DigitsSession session =
                                        DigitsSession.create(result.data, phoneNumber);
                                emailRequest(context, session);
                            } else {
                                final DigitsSession session =
                                        DigitsSession.create(result.data, phoneNumber);
                                loginSuccess(context, session, phoneNumber);
                            }
                        }
                    });
        }
    }

    private void emailRequest(final Context context, final DigitsSession session) {
        getAccountService(session).verifyAccount
                (new DigitsCallback<VerifyAccountResponse>(context, this) {
                    @Override
                    public void success(Result<VerifyAccountResponse> result) {
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

    private void startPinCodeActivity(Context context) {
        final Intent intent = new Intent(context, activityClassManager.getPinCodeActivity());
        final Bundle bundle = getBundle(phoneNumber);
        bundle.putParcelable(DigitsClient.EXTRA_RESULT_RECEIVER, resultReceiver);
        bundle.putString(DigitsClient.EXTRA_REQUEST_ID, requestId);
        bundle.putLong(DigitsClient.EXTRA_USER_ID, userId);
        bundle.putBoolean(DigitsClient.EXTRA_EMAIL, emailCollection);
        intent.putExtras(bundle);
        startActivityForResult((Activity) context, intent);
    }

    @Override
    Uri getTosUri() {
        return DigitsConstants.TWITTER_TOS;
    }

    private boolean canRequestEmail(DigitsSession newSession, DigitsSession session) {
        return emailCollection && newSession.getEmail().equals(DigitsSession.DEFAULT_EMAIL)
                && newSession.getId() == session.getId();
    }

    DigitsApiClient.AccountService getAccountService(DigitsSession session) {
        return new DigitsApiClient(session).getAccountService();
    }
}
