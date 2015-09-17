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

import android.os.Bundle;

import com.twitter.sdk.android.core.SessionManager;
import com.twitter.sdk.android.core.TwitterAuthToken;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class LoginResultReceiverTests {
    private static final String ERROR = "Big Error on login";
    static final String PHONE = "+17071234567";
    private WeakAuthCallback callback;
    private Bundle bundle;

    private ArgumentCaptor<DigitsException> digitsErrorCaptor;
    private ArgumentCaptor<DigitsSession> sessionCaptor;
    private SessionManager<DigitsSession> mockSessionManager;
    private DigitsSession session;
    private LoginResultReceiver receiver;

    @Before
    public void setUp() throws Exception {
        session = new DigitsSession(new TwitterAuthToken(TestConstants.TOKEN,
                TestConstants.SECRET), TestConstants.USER_ID, TestConstants.PHONE,
                TestConstants.EMAIL);
        mockSessionManager = mock(SessionManager.class);
        when(mockSessionManager.getActiveSession()).thenReturn(session);
        callback = mock(WeakAuthCallback.class);
        bundle = new Bundle();
        bundle.putString(LoginResultReceiver.KEY_ERROR, ERROR);
        digitsErrorCaptor = ArgumentCaptor.forClass(DigitsException.class);
        sessionCaptor = ArgumentCaptor.forClass(DigitsSession.class);
    }

    @Test
    public void testOnReceiveResult_nullListener() throws Exception {
        final LoginResultReceiver receiver = new LoginResultReceiver(null, mockSessionManager);
        receiver.onReceiveResult(LoginResultReceiver.RESULT_OK, bundle);
        receiver.onReceiveResult(LoginResultReceiver.RESULT_ERROR, bundle);
    }

    @Test
    public void testOnReceiveResult_errorResultCode() throws Exception {
        receiver = new LoginResultReceiver(callback, mockSessionManager);
        receiver.onReceiveResult(LoginResultReceiver.RESULT_ERROR, bundle);
        Mockito.verify(callback).failure(digitsErrorCaptor.capture());
        digitsErrorCaptor.getValue().getMessage().equals(ERROR);
    }

    @Test
    public void testOnReceiveResult_successResultCode() throws Exception {
        receiver = new LoginResultReceiver(callback, mockSessionManager);
        bundle.putString(DigitsClient.EXTRA_PHONE, PHONE);
        receiver.onReceiveResult(LoginResultReceiver.RESULT_OK, bundle);
        Mockito.verify(callback).success(sessionCaptor.capture(), eq(PHONE));
        assertEquals(session, sessionCaptor.getValue());
    }

    @Test
    public void testOnReceiveResult_randomResultCode() throws Exception {
        receiver = new LoginResultReceiver(callback, mockSessionManager);
        receiver.onReceiveResult(-1, bundle);
        verifyZeroInteractions(callback);
    }
}

