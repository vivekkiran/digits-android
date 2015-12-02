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
import android.os.Bundle;
import android.test.mock.MockContext;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Session;
import com.twitter.sdk.android.core.SessionManager;
import com.twitter.sdk.android.core.TwitterCore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.Locale;
import java.util.concurrent.ExecutorService;

import javax.net.ssl.SSLSocketFactory;

import io.fabric.sdk.android.Fabric;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class DigitsClientTests {
    private static final String TYPE = "typetoken";
    private static final String ANY_REQUEST_ID = "1";
    private static final String ANY_CODE = "1";
    private static int TEST_NEW_TASK_INTENT_FLAGS =
            (Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
    private static int TEST_NO_INTENT_FLAGS = 0;

    private DigitsClient digitsClient;
    private Intent capturedIntent;
    private MockContext context;
    private Digits digits;
    private TwitterCore twitterCore;
    private SessionManager<DigitsSession> sessionManager;
    private DigitsApiClient.DeviceService deviceService;
    private DigitsApiClient.SdkService sdkService;
    private DigitsController controller;
    private AuthCallback callback;
    private DigitsSession guestSession;
    private DigitsSession userSession;
    private DigitsApiClient digitsApiClient;
    private DigitsScribeClient scribeClient;
    private DigitsAuthRequestQueue authRequestQueue;
    private DigitsScribeService scribeService;
    private Fabric fabric;
    private Activity activity;
    private LoginResultReceiver loginResultReceiver;

    @Before
    public void setUp() throws Exception {
        digits = mock(Digits.class);
        sessionManager = mock(SessionManager.class);
        twitterCore = mock(TwitterCore.class);
        digitsApiClient = mock(DigitsApiClient.class);
        deviceService = mock(DigitsApiClient.DeviceService.class);
        sdkService = mock(DigitsApiClient.SdkService.class);
        context = mock(MockContext.class);
        controller = mock(DigitsController.class);
        callback = mock(AuthCallback.class);
        scribeClient = mock(DigitsScribeClient.class);
        scribeService = mock(DigitsScribeService.class);
        fabric = mock(Fabric.class);
        activity = mock(Activity.class);
        loginResultReceiver = new LoginResultReceiver(new WeakAuthCallback(callback,
                mock(DigitsScribeService.class)), sessionManager);
        authRequestQueue = createAuthRequestQueue();
        userSession = DigitsSession.create(TestConstants.DIGITS_USER, TestConstants.PHONE);
        guestSession = DigitsSession.create(TestConstants.LOGGED_OUT_USER, "");

        when(digitsApiClient.getDeviceService()).thenReturn(deviceService);
        when(digitsApiClient.getSdkService()).thenReturn(sdkService);
        when(twitterCore.getContext()).thenReturn(context);
        when(twitterCore.getSSLSocketFactory()).thenReturn(mock(SSLSocketFactory.class));
        when(digits.getExecutorService()).thenReturn(mock(ExecutorService.class));
        when(digits.getActivityClassManager()).thenReturn(new ActivityClassManagerImp());
        when(digits.getScribeClient()).thenReturn(scribeClient);
        when(digits.getFabric()).thenReturn(fabric);
        when(fabric.getCurrentActivity()).thenReturn(activity);
        when(context.getPackageName()).thenReturn(getClass().getPackage().toString());
        when(activity.getPackageName()).thenReturn(getClass().getPackage().toString());
        when(activity.isFinishing()).thenReturn(false);
        when(controller.getErrors()).thenReturn(mock(ErrorCodes.class));

        digitsClient = new DigitsClient(digits, twitterCore, sessionManager, authRequestQueue,
                scribeService) {
            @Override
            LoginResultReceiver createResultReceiver(AuthCallback callback) {
                return loginResultReceiver;
            }
        };
    }

    @Test
    public void testConstructor_nullTwitter() throws Exception {
        try {
            new DigitsClient(digits, null, sessionManager, authRequestQueue, scribeService);
            fail("Expected IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("twitter must not be null", e.getMessage());
        }
    }

    @Test
    public void testConstructor_nullDigits() throws Exception {
        try {
            new DigitsClient(null, twitterCore, sessionManager, authRequestQueue, scribeService);
            fail("Expected IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("digits must not be null", e.getMessage());
        }
    }

    @Test
    public void testConstructor_nullSessionManager() throws Exception {
        try {
            new DigitsClient(digits, twitterCore, null, authRequestQueue, scribeService);
            fail("Expected IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("sessionManager must not be null", e.getMessage());
        }
    }

    @Test
    public void testStartSignUp_withActivityContext() throws Exception {
        verifySignUp(activity, callback, TEST_NO_INTENT_FLAGS);
        verifyCallbackInReceiver(callback);
    }

    @Test
    public void testStartSignUp_withAppContext() throws Exception {
        when(fabric.getCurrentActivity()).thenReturn(null);
        verifySignUp(context, callback, TEST_NEW_TASK_INTENT_FLAGS);
        verifyCallbackInReceiver(callback);
    }

    @Test
    public void testStartSignUp_whenActivityContextIsFinishing() throws Exception {
        when(activity.isFinishing()).thenReturn(true);
        verifySignUp(context, callback, TEST_NEW_TASK_INTENT_FLAGS);
        verifyCallbackInReceiver(callback);
    }

    @Test
    public void testStartSignUp_withPhoneAndActivityContext() throws Exception {
        verifySignUpWithProvidedPhone(activity, callback, TestConstants.PHONE,
                TestConstants.ANY_BOOLEAN, TEST_NO_INTENT_FLAGS);
        verifyCallbackInReceiver(callback);
    }

    @Test
    public void testStartSignUp_withPhoneAndAppContext() throws Exception {
        when(fabric.getCurrentActivity()).thenReturn(null);
        verifySignUpWithProvidedPhone(context, callback, TestConstants.PHONE,
                TestConstants.ANY_BOOLEAN, TEST_NEW_TASK_INTENT_FLAGS);
        verifyCallbackInReceiver(callback);
    }

    @Test
    public void testStartSignUp_withNullPhone() throws Exception {
        verifySignUpWithProvidedPhone(activity, callback, null,
                TestConstants.ANY_BOOLEAN, TEST_NO_INTENT_FLAGS);
        verifyCallbackInReceiver(callback);
    }

    @Test
    public void testStartSignUp_nullListener() throws Exception {
        try {
            verifySignUp(activity, null, TEST_NO_INTENT_FLAGS);
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            assertEquals("AuthCallback must not be null",
                    ex.getMessage());
        }
    }

    @Test
    public void testStartSignUp_nullListenerWithPhone() throws Exception {
        try {
            verifySignUpWithProvidedPhone(activity, null, TestConstants.PHONE,
                    TestConstants.ANY_BOOLEAN, TEST_NO_INTENT_FLAGS);
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            assertEquals("AuthCallback must not be null",
                    ex.getMessage());
        }
    }

    @Test
    public void testStartSignUp_callbackSuccess() throws Exception {
        final DigitsAuthConfig configWithCallback = new DigitsAuthConfig.Builder()
                .withAuthCallBack(callback)
                .withPhoneNumber(TestConstants.PHONE)
                .build();
        when(sessionManager.getActiveSession()).thenReturn(userSession);
        digitsClient.startSignUp(configWithCallback);
        verify(scribeService).impression();
        verify(scribeService).success();
        verify(callback).success(userSession, null);
    }

    @Test
    public void testStartSignUp_callbackSuccessWithPhone() throws Exception {
        final DigitsAuthConfig configWithPhone = new DigitsAuthConfig.Builder()
                .withAuthCallBack(callback)
                .withEmailCollection(TestConstants.ANY_BOOLEAN)
                .build();
        when(sessionManager.getActiveSession()).thenReturn(userSession);
        digitsClient.startSignUp(configWithPhone);
        verify(scribeService).impression();
        verify(scribeService).success();
        verify(callback).success(userSession, null);
    }

    @Test
    public void testStartSignUp_loggedOutUser() throws Exception {
        when(sessionManager.getActiveSession()).thenReturn(guestSession);
        verifySignUp(activity, callback, TEST_NO_INTENT_FLAGS);
        verifyCallbackInReceiver(callback);
    }

    @Test
    public void testStartSignUp_loggedOutUserWithPhone() throws Exception {
        when(sessionManager.getActiveSession()).thenReturn(guestSession);
        verifySignUpWithProvidedPhone(activity, callback, TestConstants.PHONE,
                TestConstants.ANY_BOOLEAN, TEST_NO_INTENT_FLAGS);
        verifyCallbackInReceiver(callback);
    }

    @Test
    public void testGetApiClient_withSameSession() {
        final DigitsApiClient initial = digitsClient.getApiClient(guestSession);
        assertEquals(initial, digitsClient.getApiClient(guestSession));
    }

    @Test
    public void testGetApiClient_withDifferentSession() {
        final DigitsApiClient initial = digitsClient.getApiClient(guestSession);
        assertNotSame(initial, digitsClient.getApiClient(mock(Session.class)));
    }

    @Test
    public void testAuthDevice_withSmsVerification() throws Exception {
        final Callback listener = mock(Callback.class);
        digitsClient.authDevice(TestConstants.PHONE, Verification.sms, listener);

        verify(sdkService).auth(eq(TestConstants.PHONE), eq(Verification.sms.name()), eq(listener));
    }

    @Test
    public void testAuthDevice_withVoiceVerification() throws Exception {
        final Callback listener = mock(Callback.class);
        digitsClient.authDevice(TestConstants.PHONE, Verification.voicecall, listener);

        verify(sdkService).auth(eq(TestConstants.PHONE), eq(Verification.voicecall.name()),
                eq(listener));
    }

    @Test
    public void testRegisterDevice_withSmsVerification() throws Exception {
        final Callback listener = mock(Callback.class);
        digitsClient.registerDevice(TestConstants.PHONE, Verification.sms, listener);
        verify(deviceService).register(TestConstants.PHONE,
                DigitsClient.THIRD_PARTY_CONFIRMATION_CODE, true, Locale.getDefault().getLanguage(),
                DigitsClient.CLIENT_IDENTIFIER, Verification.sms.name(), listener);
    }

    @Test
    public void testRegisterDevice_withVoiceVerification() throws Exception {
        final Callback listener = mock(Callback.class);
        digitsClient.registerDevice(TestConstants.PHONE, Verification.voicecall, listener);
        verify(deviceService).register(TestConstants.PHONE,
                DigitsClient.THIRD_PARTY_CONFIRMATION_CODE, true, Locale.getDefault().getLanguage(),
                DigitsClient.CLIENT_IDENTIFIER, Verification.voicecall.name(), listener);
    }

    @Test
    public void testLoginDevice() throws Exception {
        final Callback listener = mock(Callback.class);
        digitsClient.loginDevice(ANY_REQUEST_ID, TestConstants.USER_ID, ANY_CODE, listener);
        verify(sdkService).login(eq(ANY_REQUEST_ID), eq(TestConstants.USER_ID), eq(ANY_CODE),
                eq(listener));
    }

    @Test
    public void testVerifyPin() throws Exception {
        final Callback listener = mock(Callback.class);
        digitsClient.verifyPin(ANY_REQUEST_ID, TestConstants.USER_ID, ANY_CODE, listener);
        verify(sdkService).verifyPin(eq(ANY_REQUEST_ID), eq(TestConstants.USER_ID), eq(ANY_CODE),
                eq(listener));
    }

    private void verifyCallbackInReceiver(AuthCallback expected) {
        final LoginResultReceiver receiver = capturedIntent.getParcelableExtra(DigitsClient
                .EXTRA_RESULT_RECEIVER);
        assertEquals(expected, receiver.callback.getCallback());
    }

    private void verifySignUp(Context context, AuthCallback callback, int expectedFlags) {
        final DigitsAuthConfig configWithCallback = new DigitsAuthConfig.Builder()
                .withAuthCallBack(callback)
                .build();
        final Bundle expectedBundle = createBundle(loginResultReceiver, "", false);

        digitsClient.startSignUp(configWithCallback);
        verify(scribeService).impression();
        verifyNoMoreInteractions(scribeService);
        final ArgumentCaptor<Intent> argument = ArgumentCaptor.forClass(Intent.class);
        //verify start activity is called, passing an ArgumentCaptor to get the intent and check
        // if it's correctly build
        verify(context).startActivity(argument.capture());
        capturedIntent = argument.getValue();
        assertEquals(expectedFlags, capturedIntent.getFlags());
        assertEquals(expectedBundle, capturedIntent.getExtras());
    }

    private void verifySignUpWithProvidedPhone(Context context, AuthCallback callback, String phone,
                                               boolean emailCollection, int expectedFlags) {
        final DigitsAuthConfig digitsAuthConfig = new DigitsAuthConfig.Builder()
                .withAuthCallBack(callback)
                .withEmailCollection(emailCollection)
                .withPhoneNumber(phone)
                .build();

        final Bundle expectedBundle = createBundle(loginResultReceiver, phone, emailCollection);

        digitsClient.startSignUp(digitsAuthConfig);
        verify(scribeService).impression();
        verifyNoMoreInteractions(scribeService);
        final ArgumentCaptor<Intent> argument = ArgumentCaptor.forClass(Intent.class);
        //verify start activity is called, passing an ArgumentCaptor to get the intent and check
        // if it's correctly build
        verify(context).startActivity(argument.capture());
        capturedIntent = argument.getValue();
        assertEquals(expectedFlags, capturedIntent.getFlags());
        assertEquals(expectedBundle, capturedIntent.getExtras());
    }

    private DigitsAuthRequestQueue createAuthRequestQueue() {
        final DigitsAuthRequestQueue authRequestQueue = mock(DigitsAuthRequestQueue.class);
        when(authRequestQueue.addClientRequest(any(Callback.class))).thenAnswer(
                new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {
                        ((Callback<DigitsApiClient>) invocation.getArguments()[0])
                                .success(digitsApiClient, null);
                        return null;
                    }
                }
        );
        return authRequestQueue;
    }

    private Bundle createBundle(LoginResultReceiver loginResultReceiver,
                                String defaultPhone, Boolean emailRequired){
        final Bundle bundle = new Bundle();

        bundle.putParcelable(DigitsClient.EXTRA_RESULT_RECEIVER, loginResultReceiver);
        bundle.putString(DigitsClient.EXTRA_PHONE, defaultPhone == null ? "": defaultPhone);
        bundle.putBoolean(DigitsClient.EXTRA_EMAIL, emailRequired);

        return bundle;
    }
}
