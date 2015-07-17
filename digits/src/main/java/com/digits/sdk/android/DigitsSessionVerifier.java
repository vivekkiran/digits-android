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

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Session;
import com.twitter.sdk.android.core.SessionManager;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.internal.SessionVerifier;

class DigitsSessionVerifier implements SessionVerifier {
    private final VerificationHandler verificationHandler;

    DigitsSessionVerifier(SessionListener sessionListener) {
        this(new VerificationHandler(sessionListener));
    }

    DigitsSessionVerifier(VerificationHandler verificationHandler) {
        this.verificationHandler = verificationHandler;
    }

    @Override
    public void verifySession(final Session session) {
        if (session instanceof DigitsSession && !((DigitsSession) session).isLoggedOutUser()) {
            final DigitsApiClient.AccountService service = getAccountService(session);
            verificationHandler.setSession((DigitsSession) session);
            service.verifyAccount(verificationHandler);
        }
    }

    DigitsApiClient.AccountService getAccountService(Session session) {
        return new DigitsApiClient(session).getAccountService();
    }


    static class VerificationHandler extends Callback<VerifyAccountResponse> {
        private final SessionListener sessionListener;
        private final SessionManager<DigitsSession> sessionManager;
        private DigitsSession session;

        public VerificationHandler(SessionListener sessionListener) {
            this(sessionListener, Digits.getSessionManager());
        }

        public VerificationHandler(SessionListener sessionListener,
                                   SessionManager<DigitsSession> sessionManager) {
            this.sessionListener = sessionListener;
            this.sessionManager = sessionManager;
        }

        public void setSession(DigitsSession session) {
            this.session = session;
        }

        @Override
        public void success(Result<VerifyAccountResponse> result) {
            if (result.data != null) {
                final DigitsSession newSession = DigitsSession.create(result.data);
                sessionManager.setActiveSession(newSession);
                sessionListener.changed(newSession);
            }
        }

        @Override
        public void failure(TwitterException exception) {
            //Ignore failure
        }
    }

}
