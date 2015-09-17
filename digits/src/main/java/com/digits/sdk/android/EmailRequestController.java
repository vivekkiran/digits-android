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
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.EditText;

import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.SessionManager;

import java.util.regex.Matcher;

import io.fabric.sdk.android.services.common.CommonUtils;

public class EmailRequestController extends DigitsControllerImpl {
    private String phoneNumber;

    EmailRequestController(StateButton stateButton, EditText editText,
                           ResultReceiver resultReceiver, String phoneNumber,
                           DigitsScribeService scribeService) {
        this(resultReceiver, stateButton, editText, Digits.getSessionManager(),
                Digits.getInstance().getActivityClassManager(), new DigitsClient(), phoneNumber,
                scribeService, new EmailErrorCodes(stateButton.getContext().getResources()));
    }

    EmailRequestController(ResultReceiver resultReceiver, StateButton stateButton,
                           EditText editText, SessionManager<DigitsSession> sessionManager,
                           ActivityClassManager activityClassManager, DigitsClient client,
                           String phoneNumber, DigitsScribeService scribeService,
                           ErrorCodes emailErrorCodes) {
        super(resultReceiver, stateButton, editText, client, emailErrorCodes,
                activityClassManager, sessionManager, scribeService);
        this.phoneNumber = phoneNumber;
    }

    @Override
    Uri getTosUri() {
        return DigitsConstants.DIGITS_TOS;
    }

    @Override
    public void executeRequest(final Context context) {
        scribeService.click(DigitsScribeConstants.Element.SUBMIT);
        if (validateInput(editText.getText())) {
            sendButton.showProgress();
            CommonUtils.hideKeyboard(context, editText);
            final String email = editText.getText().toString();
            final DigitsSession session = sessionManager.getActiveSession();
            if (session != null && !session.isLoggedOutUser()) {
                final DigitsApiClient.SdkService service =
                        getSdkService(session);
                service.email(email, new DigitsCallback<DigitsSessionResponse>(context, this) {
                    @Override
                    public void success(Result<DigitsSessionResponse> result) {
                        scribeService.success();
                        loginSuccess(context, session, phoneNumber);
                    }
                });
            } else {
                handleError(context, new UnrecoverableException(""));
            }
        } else {
            editText.setError(context.getString(R.string.dgts__invalid_email));
        }
    }

    DigitsApiClient.SdkService getSdkService(DigitsSession session) {
        return new DigitsApiClient(session).getSdkService();
    }

    @Override
    public boolean validateInput(CharSequence text) {
        return !TextUtils.isEmpty(text) && validate(text.toString());
    }

    private boolean validate(String email) {
        final Matcher matcher = Patterns.EMAIL_ADDRESS.matcher(email);
        return matcher.find();
    }
}
