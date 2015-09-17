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
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Session;
import com.twitter.sdk.android.core.SessionManager;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DigitsClient {
    public static final String EXTRA_PHONE = "phone_number";
    public static final String EXTRA_RESULT_RECEIVER = "receiver";
    public static final String EXTRA_REQUEST_ID = "request_id";
    public static final String EXTRA_USER_ID = "user_id";
    public static final String THIRD_PARTY_CONFIRMATION_CODE = "third_party_confirmation_code";
    public static final String EXTRA_FALLBACK_REASON = "fallback_reason";
    public static final String EXTRA_AUTH_CONFIG = "auth_config";
    public static final String EXTRA_EMAIL = "email_enabled";
    public static final String CLIENT_IDENTIFIER = "digits_sdk";

    private final Digits digits;
    private final SessionManager<DigitsSession> sessionManager;
    private final TwitterCore twitterCore;
    private final DigitsAuthRequestQueue authRequestQueue;
    private final DigitsScribeService scribeService;
    private DigitsApiClient digitsApiClient;

    DigitsClient() {
        this(Digits.getInstance(), TwitterCore.getInstance(), Digits.getSessionManager(), null,
                new AuthScribeService(Digits.getInstance().getScribeClient()));
    }

    DigitsClient(Digits digits, TwitterCore twitterCore, SessionManager<DigitsSession>
            sessionManager, DigitsAuthRequestQueue authRequestQueue,
                 DigitsScribeService scribeService) {
        if (twitterCore == null) {
            throw new IllegalArgumentException("twitter must not be null");
        }
        if (digits == null) {
            throw new IllegalArgumentException("digits must not be null");
        }
        if (sessionManager == null) {
            throw new IllegalArgumentException("sessionManager must not be null");
        }

        this.twitterCore = twitterCore;
        this.digits = digits;
        this.sessionManager = sessionManager;

        if (authRequestQueue == null) {
            this.authRequestQueue = createAuthRequestQueue(sessionManager);
            this.authRequestQueue.sessionRestored(null);
        } else {
            this.authRequestQueue = authRequestQueue;
        }
        this.scribeService = scribeService;
    }

    protected DigitsAuthRequestQueue createAuthRequestQueue(SessionManager sessionManager) {
        final List<SessionManager<? extends Session>> sessionManagers = new ArrayList<>(1);
        sessionManagers.add(sessionManager);
        final DigitsGuestSessionProvider sessionProvider =
                new DigitsGuestSessionProvider(sessionManager, sessionManagers);
        return new DigitsAuthRequestQueue(this, sessionProvider);
    }

    protected void startSignUp(AuthCallback callback) {
        startSignUpWithBundle(callback, createBundleForAuthFlow(callback));
    }

    protected void startSignUp(AuthCallback callback, String phoneNumber,
                               boolean emailCollection) {
        startSignUpWithBundle(callback, createBundleForAuthFlow(callback,
                phoneNumber == null ? "" : phoneNumber, emailCollection));
    }

    private void startSignUpWithBundle(AuthCallback callback, Bundle bundle) {
        scribeService.impression();
        final DigitsSession session = sessionManager.getActiveSession();
        if (session != null && !session.isLoggedOutUser()) {
            callback.success(session, null);
            scribeService.success();
        } else {
            startPhoneNumberActivity(twitterCore.getContext(), bundle);
        }
    }

    private Bundle createBundleForAuthFlow(AuthCallback callback, String phoneNumber,
                                           boolean emailCollection) {
        final Bundle bundle = createBundleForAuthFlow(callback);
        bundle.putString(DigitsClient.EXTRA_PHONE, phoneNumber);
        bundle.putBoolean(DigitsClient.EXTRA_EMAIL, emailCollection);
        return bundle;
    }

    private Bundle createBundleForAuthFlow(AuthCallback callback) {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(DigitsClient.EXTRA_RESULT_RECEIVER,
                createResultReceiver(callback));
        return bundle;
    }

    LoginResultReceiver createResultReceiver(AuthCallback callback) {
        return new LoginResultReceiver(callback, sessionManager);
    }

    private void startPhoneNumberActivity(Context context, Bundle bundle) {
        final Intent intent = new Intent(context, digits.getActivityClassManager()
                .getPhoneNumberActivity());
        intent.putExtras(bundle);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    protected void authDevice(final String phoneNumber, final Verification verificationType,
            final Callback<AuthResponse> callback) {
        authRequestQueue.addClientRequest(new CallbackWrapper<AuthResponse>(callback) {

            @Override
            public void success(Result<DigitsApiClient> result) {
                result.data.getSdkService().auth(phoneNumber, verificationType.name(), callback);
            }

        });
    }

    protected void createAccount(final String pin, final String phoneNumber,
            final Callback<DigitsUser> callback) {
        authRequestQueue.addClientRequest(new CallbackWrapper<DigitsUser>(callback) {

            @Override
            public void success(Result<DigitsApiClient> result) {
                result.data.getSdkService().account(phoneNumber, pin, callback);
            }

        });
    }

    protected void loginDevice(final String requestId, final long userId, final String code,
            final Callback<DigitsSessionResponse> callback) {
        authRequestQueue.addClientRequest(new CallbackWrapper<DigitsSessionResponse>(callback) {

            @Override
            public void success(Result<DigitsApiClient> result) {
                result.data.getSdkService().login(requestId, userId, code, callback);
            }

        });
    }

    protected void registerDevice(final String phoneNumber, final Verification verificationType,
                                  final Callback<DeviceRegistrationResponse> callback) {
        authRequestQueue.addClientRequest(
                new CallbackWrapper<DeviceRegistrationResponse>(callback) {

            @Override
            public void success(Result<DigitsApiClient> result) {
                result.data.getDeviceService().register(phoneNumber, THIRD_PARTY_CONFIRMATION_CODE,
                        true, Locale.getDefault().getLanguage(), CLIENT_IDENTIFIER,
                        verificationType.name(), callback);
            }

        });
    }

    protected void verifyPin(final String requestId, final long userId, final String pin,
            final Callback<DigitsSessionResponse> callback) {
        authRequestQueue.addClientRequest(new CallbackWrapper<DigitsSessionResponse>(callback) {

            @Override
            public void success(Result<DigitsApiClient> result) {
                result.data.getSdkService().verifyPin(requestId, userId, pin, callback);
            }

        });
    }

    static abstract class CallbackWrapper<T> extends Callback<DigitsApiClient> {
        final Callback<T> callback;

        public CallbackWrapper(Callback<T> callback) {
            this.callback = callback;
        }

        @Override
        public void failure(TwitterException exception) {
            if (callback != null) {
                callback.failure(exception);
            }
        }
    }

    DigitsApiClient getApiClient(Session session) {
        if (digitsApiClient != null && digitsApiClient.getSession().equals(session)) {
            return digitsApiClient;
        }

        digitsApiClient = new DigitsApiClient(session, twitterCore.getAuthConfig(),
                twitterCore.getSSLSocketFactory(), digits.getExecutorService(),
                new DigitsUserAgent(digits.getVersion(), Build.VERSION.RELEASE));

        return digitsApiClient;
    }
}
