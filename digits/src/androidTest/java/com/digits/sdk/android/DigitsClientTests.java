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

import android.content.ComponentName;
import android.content.Intent;
import android.test.mock.MockContext;

import io.fabric.sdk.android.FabricTestUtils;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.SessionManager;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.internal.oauth.OAuth2Service;
import com.twitter.sdk.android.core.internal.oauth.OAuth2Token;

import org.mockito.ArgumentCaptor;

import java.util.concurrent.ExecutorService;

import javax.net.ssl.SSLSocketFactory;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class DigitsClientTests extends DigitsAndroidTestCase {
    private static final String TYPE = "typetoken";
    private static final String ANY_REQUEST_ID = "1";
    private static final String ANY_CODE = "1";
    private DigitsClient digitsClient;
    private Intent capturedIntent;
    private MockContext context;
    private ComponentName component;
    private OAuth2Service authService;
    private Digits digits;
    private TwitterCore twitterCore;
    private SessionManager<DigitsSession> sessionManager;
    private DigitsApiProvider.DeviceService deviceService;
    private DigitsApiProvider.SdkService sdkService;
    private DigitsController controller;
    private AuthCallback callback;
    private DigitsSession guestSession;
    private DigitsSession userSession;
    private DigitsApiProvider digitsApiProvider;
    private DigitsScribeService scribeService;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        digits = mock(Digits.class);
        sessionManager = mock(SessionManager.class);
        twitterCore = mock(TwitterCore.class);
        digitsApiProvider = mock(MockDigitsApiProvider.class);
        deviceService = mock(DigitsApiProvider.DeviceService.class);
        sdkService = mock(DigitsApiProvider.SdkService.class);
        context = mock(MockContext.class);
        authService = mock(OAuth2Service.class);
        controller = mock(DigitsController.class);
        callback = mock(AuthCallback.class);
        scribeService = mock(DigitsScribeService.class);

        userSession = DigitsSession.create(TestConstants.DIGITS_USER);
        guestSession = DigitsSession.create(TestConstants.LOGGED_OUT_USER);

        when(digitsApiProvider.getDeviceService()).thenReturn(deviceService);
        when(digitsApiProvider.getSdkService()).thenReturn(sdkService);
        when(twitterCore.getContext()).thenReturn(context);
        when(twitterCore.getSSLSocketFactory()).thenReturn(mock(SSLSocketFactory.class));
        when(digits.getExecutorService()).thenReturn(mock(ExecutorService.class));
        when(digits.getActivityClassManager()).thenReturn(new ActivityClassManagerImp());
        when(digits.getScribeService()).thenReturn(scribeService);
        when(context.getPackageName()).thenReturn(getClass().getPackage().toString());
        when(controller.getErrors()).thenReturn(mock(ErrorCodes.class));
        component = new ComponentName(context, new ActivityClassManagerImp()
                .getPhoneNumberActivity());

        digitsClient = new DigitsClient(digits, twitterCore, sessionManager, authService,
                digitsApiProvider);
    }

    @Override
    protected void tearDown() throws Exception {
        FabricTestUtils.resetFabric();
        super.tearDown();
    }

    public void testAuthDevice_success() throws Exception {
        final Callback callback = mock(Callback.class);
        final Result<OAuth2Token> result = new Result<>(new OAuth2Token(TYPE, TOKEN), null);
        final DigitsCallback authCallback = authDevice(callback);
        authCallback.success(result);
        verify(sessionManager).setSession(anyLong(), any(DigitsSession.class));
        verifyNoMoreInteractions(sdkService);
        assertNotSame(digitsClient.digitsApiProvider, digitsApiProvider);
    }

    public void testAuthDevice_failure() throws Exception {
        final Callback callback = mock(Callback.class);
        final DigitsCallback authCallback = authDevice(callback);
        authCallback.failure(new TwitterException("Exception"));
        verifyNoMoreInteractions(sdkService, deviceService);
        verify(controller).handleError(eq(context), any(DigitsException.class));
    }

    private DigitsCallback authDevice(Callback callback) {
        final ArgumentCaptor<DigitsCallback> argumentCaptor = ArgumentCaptor.forClass
                (DigitsCallback.class);

        digitsClient.authDevice(context, controller, PHONE, callback);
        verify(authService).requestGuestOrAppAuthToken(argumentCaptor.capture());
        return argumentCaptor.getValue();
    }

    public void testConstructor_nullTwitter() throws Exception {
        try {
            new DigitsClient(digits, null, sessionManager, authService, digitsApiProvider);
            fail("Expected IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("twitter must not be null", e.getMessage());
        }
    }

    public void testConstructor_nullDigits() throws Exception {
        try {
            new DigitsClient(null, twitterCore, sessionManager, authService, digitsApiProvider);
            fail("Expected IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("digits must not be null", e.getMessage());
        }
    }

    public void testConstructor_nullSessionManager() throws Exception {
        try {
            new DigitsClient(digits, twitterCore, null, authService, digitsApiProvider);
            fail("Expected IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("sessionManager must not be null", e.getMessage());
        }
    }

    public void testConstructor_nullAuthService() throws Exception {
        try {
            new DigitsClient(digits, twitterCore, sessionManager, null, digitsApiProvider);
            fail("Expected IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("authService must not be null", e.getMessage());
        }
    }

    public void testStartSignUp() throws Exception {
        verifySignUp(callback);
        verifyCallbackInReceiver(callback);
    }

    public void testStartSignUp_withPhone() throws Exception {
        verifySignUpWithProvidedPhone(callback, PHONE);
        verifyCallbackInReceiver(callback);
    }

    public void testStartSignUp_withNullPhone() throws Exception {
        verifySignUpWithProvidedPhone(callback, null);
        verifyCallbackInReceiver(callback);
    }

    public void testStartSignUp_nullListener() throws Exception {
        verifySignUp(null);
        verifyCallbackInReceiver(null);
    }

    public void testStartSignUp_nullListenerWithPhone() throws Exception {
        verifySignUpWithProvidedPhone(null, PHONE);
        verifyCallbackInReceiver(null);
    }

    public void testStartSignUp_callbackSuccess() throws Exception {
        when(sessionManager.getActiveSession()).thenReturn(userSession);
        digitsClient.startSignUp(callback);
        verify(callback).success(userSession, null);
    }

    public void testStartSignUp_callbackSuccessWithPhone() throws Exception {
        when(sessionManager.getActiveSession()).thenReturn(userSession);
        digitsClient.startSignUp(callback, PHONE);
        verify(callback).success(userSession, null);
    }

    public void testStartSignUp_loggedOutUser() throws Exception {
        when(sessionManager.getActiveSession()).thenReturn(guestSession);
        verifySignUp(callback);
        verifyCallbackInReceiver(callback);
    }

    public void testStartSignUp_loggedOutUserWithPhone() throws Exception {
        when(sessionManager.getActiveSession()).thenReturn(guestSession);
        verifySignUpWithProvidedPhone(callback, PHONE);
        verifyCallbackInReceiver(callback);
    }

    public void testRegisterDevice() throws Exception {
        final Callback listener = mock(Callback.class);
        digitsClient.registerDevice(PHONE, listener);
        verify(deviceService).register(PHONE, DigitsClient.THIRD_PARTY_CONFIRMATION_CODE, true,
                listener);
    }

    public void testLoginDevice() throws Exception {
        final Callback listener = mock(Callback.class);
        digitsClient.loginDevice(ANY_REQUEST_ID, USER_ID, ANY_CODE, listener);
        verify(sdkService).login(eq(ANY_REQUEST_ID), eq(USER_ID), eq(ANY_CODE), eq(listener));
    }


    public void testVerifyPin() throws Exception {
        final Callback listener = mock(Callback.class);
        digitsClient.verifyPin(ANY_REQUEST_ID, USER_ID, ANY_CODE, listener);
        verify(sdkService).verifyPin(eq(ANY_REQUEST_ID), eq(USER_ID), eq(ANY_CODE), eq(listener));
    }

    private void verifyCallbackInReceiver(AuthCallback expected) {
        final LoginResultReceiver receiver = capturedIntent.getParcelableExtra(DigitsClient
                .EXTRA_RESULT_RECEIVER);
        assertEquals(expected, receiver.callback.getCallback());
    }

    private void verifySignUp(AuthCallback callback) {
        digitsClient.startSignUp(callback);
        verify(scribeService).dailyPing();
        final ArgumentCaptor<Intent> argument = ArgumentCaptor.forClass(Intent.class);
        //verify start activity is called, passing an ArgumentCaptor to get the intent and check
        // if it's correctly build
        verify(context).startActivity(argument.capture());
        capturedIntent = argument.getValue();
        assertTrue(component.equals(capturedIntent.getComponent()));
    }

    private void verifySignUpWithProvidedPhone(AuthCallback callback, String phone) {
        digitsClient.startSignUp(callback, phone);
        verify(scribeService).dailyPing();
        final ArgumentCaptor<Intent> argument = ArgumentCaptor.forClass(Intent.class);
        //verify start activity is called, passing an ArgumentCaptor to get the intent and check
        // if it's correctly build
        verify(context).startActivity(argument.capture());
        capturedIntent = argument.getValue();
        assertEquals(phone == null ? "" : phone, capturedIntent.getStringExtra(DigitsClient
                .EXTRA_PHONE));
        assertTrue(component.equals(capturedIntent.getComponent()));
    }
}
