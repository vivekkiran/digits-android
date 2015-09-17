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

import android.os.ResultReceiver;
import android.widget.EditText;

import com.twitter.sdk.android.core.SessionManager;

import static org.mockito.Mockito.mock;

public class DummyLoginCodeController extends LoginCodeController {
    private final DigitsApiClient.AccountService accountService;

    public DummyLoginCodeController(ResultReceiver resultReceiver, StateButton sendButton,
                                    EditText phoneEditText,
                                    SessionManager<DigitsSession> sessionManager,
                                    DigitsClient digitsClient, String requestId,
                                    long userId, String phoneWithCountryCode, ErrorCodes errors,
                                    ActivityClassManagerImp activityClassManagerImp,
                                    DigitsScribeService scribeService, boolean emailCollection) {
        super(resultReceiver, sendButton, phoneEditText, sessionManager, digitsClient, requestId,
                userId, phoneWithCountryCode, errors, activityClassManagerImp, scribeService,
                emailCollection);
        accountService = mock(DigitsApiClient.AccountService.class);
    }


    @Override
    DigitsApiClient.AccountService getAccountService(DigitsSession session) {
        return accountService;
    }
}
