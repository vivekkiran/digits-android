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
import com.twitter.sdk.android.core.TwitterAuthToken;

import org.apache.http.HttpStatus;

import java.util.ArrayList;

import retrofit.client.Header;
import retrofit.client.Response;

public class DigitsSessionTests extends DigitsAndroidTestCase {

    private static final String ANY_HEADER = "header";
    private static final String ANY_DATA = "data";

    public void testCreate_user() throws Exception {
        final ArrayList<Header> headers = new ArrayList<>();
        headers.add(new Header(ANY_HEADER, ANY_DATA));
        headers.add(new Header(DigitsSession.TOKEN_HEADER, TOKEN));
        headers.add(new Header(DigitsSession.SECRET_HEADER, SECRET));

        final Response response = new Response(TWITTER_URL, HttpStatus.SC_ACCEPTED, "", headers
                , null);
        final DigitsUser user = new DigitsUser(USER_ID, "");
        final DigitsSession session = DigitsSession.create(new Result<>(user, response));
        final DigitsSession newSession = new DigitsSession(new TwitterAuthToken(TOKEN, SECRET),
                USER_ID);
        assertEquals(session, newSession);
    }

    public void testCreate_digitsUser() throws Exception {
        final DigitsSessionResponse response = getNewDigitsSessionResponse();
        final DigitsSession session = DigitsSession.create(response);
        final DigitsSession newSession = new DigitsSession(new TwitterAuthToken(TOKEN, SECRET),
                USER_ID);
        assertEquals(session, newSession);
    }

    static DigitsSessionResponse getNewLoggedOutUser() {
        return getDigitsSessionResponse(TOKEN, SECRET, DigitsSession.LOGGED_OUT_USER_ID);
    }

    static DigitsSessionResponse getNewDigitsSessionResponse() {
        return getDigitsSessionResponse(TOKEN, SECRET, USER_ID);
    }

    private static DigitsSessionResponse getDigitsSessionResponse(String token, String secret,
            long userId) {
        final DigitsSessionResponse response = new DigitsSessionResponse();
        response.token = token;
        response.secret = secret;
        response.userId = userId;
        return response;
    }

    public void testCreate_nullDigitsSessionResponse() throws Exception {
        try {
            final DigitsSessionResponse response = null;
            DigitsSession.create(response);
            fail();
        } catch (IllegalArgumentException ex) {
            assertEquals("result must not be null", ex.getMessage());
        }
    }

    public void testCreate_nullResult() throws Exception {
        try {
            final Result result = null;
            DigitsSession.create(result);
            fail();
        } catch (IllegalArgumentException ex) {
            assertEquals("result must not be null", ex.getMessage());
        }
    }

    public void testCreate_nullResultData() throws Exception {
        try {
            final Response response = new Response(TWITTER_URL, HttpStatus.SC_ACCEPTED, "",
                    new ArrayList<Header>(), null);
            DigitsSession.create(new Result<DigitsUser>(null, response));
            fail();
        } catch (IllegalArgumentException ex) {
            assertEquals("result.data must not be null", ex.getMessage());
        }
    }

    public void testCreate_nullResultResponse() throws Exception {
        try {
            DigitsSession.create(new Result<>(new DigitsUser(USER_ID, ""), null));
            fail();
        } catch (IllegalArgumentException ex) {
            assertEquals("result.response must not be null", ex.getMessage());
        }
    }

    public void testIsLoggedOutUser_false() throws Exception {
        final DigitsSession session = DigitsSession.create(getNewDigitsSessionResponse());
        assertFalse(session.isLoggedOutUser());
    }

    public void testIsLoggedOutUser_true() throws Exception {
        final DigitsSession session = DigitsSession.create(getNewLoggedOutUser());
        assertTrue(session.isLoggedOutUser());
    }
}
