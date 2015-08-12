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

import java.util.concurrent.ConcurrentHashMap;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class VerificationCallbackTest {
    private SessionListener sessionListener;
    private SessionManager<DigitsSession> sessionManager;
    private DigitsSessionVerifier.VerificationCallback verificationCallback;
    private Result<VerifyAccountResponse> result;
    private ConcurrentHashMap<SessionListener, Boolean> sessionListenerMap;
    private SessionListener sessionListener2;
    private Result<VerifyAccountResponse> resultInvalidData;

    @Before
    public void setUp() throws Exception {
        sessionListener = mock(SessionListener.class);
        sessionListener2 = mock(SessionListener.class);
        sessionManager = mock(SessionManager.class);
        sessionListenerMap = new ConcurrentHashMap<>();
        verificationCallback = new DigitsSessionVerifier.VerificationCallback
                (sessionListenerMap, sessionManager);
        verificationCallback.addSessionListener(sessionListener);
        result = new Result<>(TestConstants.getVerifyAccountResponse(), null);
        resultInvalidData = new Result<>(TestConstants.getInvalidVerifyAccountResponse(), null);
    }

    @Test
    public void testSuccess() throws Exception {
        verificationCallback.addSessionListener(sessionListener);
        verificationCallback.addSessionListener(sessionListener2);
        verificationCallback.success(result);
        verify(sessionManager).setSession(TestConstants.USER_ID,
                DigitsSession.create(result.data));
        verify(sessionListener).changed(any(DigitsSession.class));
        verify(sessionListener2).changed(any(DigitsSession.class));
    }

    public void testSuccess_nullListener() throws Exception {
        sessionListenerMap.put(null, Boolean.TRUE);
        verificationCallback.success(result);
        verify(sessionManager).setSession(TestConstants.USER_ID,
                DigitsSession.create(result.data));
        verifyZeroInteractions(sessionListener);
        verifyZeroInteractions(sessionManager);
    }

    @Test
    public void testSuccess_nullResultData() throws Exception {
        verificationCallback.addSessionListener(sessionListener);
        verificationCallback.success(new Result<VerifyAccountResponse>(null, null));
        verifyZeroInteractions(sessionListener);
        verifyZeroInteractions(sessionManager);
    }

    @Test
    public void testSuccess_invalidUser() throws Exception {
        verificationCallback.addSessionListener(sessionListener);
        verificationCallback.success(resultInvalidData);
        verifyZeroInteractions(sessionListener);
        verifyZeroInteractions(sessionManager);
    }

    @Test(expected = NullPointerException.class)
    public void testAddSessionListener_nullObject() throws Exception {
        verificationCallback.addSessionListener(null);
    }

    @Test(expected = NullPointerException.class)
    public void testRemoveSessionListener_nullObject() throws Exception {
        verificationCallback.removeSession(null);
    }
}
