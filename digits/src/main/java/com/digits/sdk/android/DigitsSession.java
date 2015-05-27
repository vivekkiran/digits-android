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

import android.text.TextUtils;

import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.services.persistence.SerializationStrategy;

import com.twitter.sdk.android.core.AuthToken;
import com.twitter.sdk.android.core.AuthTokenAdapter;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Session;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.internal.oauth.OAuth2Token;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit.client.Header;

/**
 * Defines class for digits user session
 * has an {@link com.twitter.sdk.android.core.AuthToken} and user id
 */
public class DigitsSession extends Session<AuthToken> {
    public static final long UNKNOWN_USER_ID = -1L;
    public static final long LOGGED_OUT_USER_ID = 0L;

    static final String TOKEN_HEADER = "x-twitter-new-account-oauth-access-token";
    static final String SECRET_HEADER = "x-twitter-new-account-oauth-secret";


    public DigitsSession(AuthToken authToken, long id) {
        super(authToken, id);
    }

    public DigitsSession(OAuth2Token token) {
        this(token, DigitsSession.LOGGED_OUT_USER_ID);
    }

    public boolean isLoggedOutUser() {
        return getId() == DigitsSession.LOGGED_OUT_USER_ID;
    }

    static DigitsSession create(Result<DigitsUser> result) {
        if (result == null) {
            throw new IllegalArgumentException("result must not be null");
        }
        if (result.data == null) {
            throw new IllegalArgumentException("result.data must not be null");
        }
        if (result.response == null) {
            throw new IllegalArgumentException("result.response must not be null");
        }

        final List<Header> headers = result.response.getHeaders();
        String token = "";
        String secret = "";
        for (Header header : headers) {
            if (TOKEN_HEADER.equals(header.getName())) {
                token = header.getValue();
            } else if (SECRET_HEADER.equals(header.getName())) {
                secret = header.getValue();
            }
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(secret)) {
                break;
            }
        }

        return new DigitsSession(new TwitterAuthToken(token, secret), result.data.id);
    }

    static DigitsSession create(DigitsSessionResponse result) {
        if (result == null) {
            throw new IllegalArgumentException("result must not be null");
        }

        return new DigitsSession(new TwitterAuthToken(result.token, result.secret), result.userId);
    }

    public static DigitsSession create(TwitterSession session) {
        return new DigitsSession(session.getAuthToken(), session.getUserId());
    }

    public static class Serializer implements SerializationStrategy<DigitsSession> {

        private static final String TAG = "Digits";
        private final Gson gson;

        public Serializer() {
            this.gson = new GsonBuilder()
                    .registerTypeAdapter(AuthToken.class, new AuthTokenAdapter())
                    .create();
        }

        @Override
        public String serialize(DigitsSession session) {
            if (session != null && session.getAuthToken() != null) {
                try {
                    return gson.toJson(session);
                } catch (Exception e) {
                    Fabric.getLogger().d(TAG, e.getMessage());
                }
            }
            return "";
        }

        @Override
        public DigitsSession deserialize(String serializedSession) {
            if (!TextUtils.isEmpty(serializedSession)) {
                try {
                    return gson.fromJson(serializedSession, DigitsSession.class);
                } catch (Exception e) {
                    Fabric.getLogger().d(TAG, e.getMessage());
                }
            }
            return null;
        }

    }
}
