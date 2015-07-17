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

import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.SessionManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 21)
public class VerificationHandlerTest {
    private SessionListener sessionListener;
    private SessionManager sessionManager;
    private DigitsSessionVerifier.VerificationHandler verificationHandler;
    private Result<VerifyAccountResponse> result;
    private DigitsSession session;

    @Before
    public void setUp() throws Exception {
        session = DigitsSession.create(TestConstants.getVerifyAccountResponse());
        sessionListener = mock(SessionListener.class);
        sessionManager = mock(SessionManager.class);
        verificationHandler = new DigitsSessionVerifier.VerificationHandler
                (sessionListener, sessionManager);
        verificationHandler.setSession(session);
        result = new Result<>(TestConstants.getVerifyAccountResponse(), null);
    }

    @Test
    public void testSuccess() throws Exception {
        verificationHandler.success(result);
        verify(sessionManager).setActiveSession(any(DigitsSession.class));
        verify(sessionListener).changed(any(DigitsSession.class));
    }
}
