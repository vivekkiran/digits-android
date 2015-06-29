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

import com.twitter.sdk.android.core.AuthTokenUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 21)
public class DigitsSessionSerializerTests {
    private static final long CREATED_AT = 1414450780L;
    public static final String SESSION_JSON = "{\"auth_token\":{\"auth_type\":\"oauth1a\","
            + "\"auth_token\":{"
            + "\"token\":\"token\","
            + "\"secret\":\"secret\","
            + "\"created_at\":" + CREATED_AT + "}},"
            + "\"id\":-1}";
    public static final String FULL_SESSION_JSON = "{\"auth_token\":{\"auth_type\":\"oauth1a\","
            + "\"auth_token\":{"
            + "\"token\":\"token\","
            + "\"secret\":\"secret\","
            + "\"created_at\":" + CREATED_AT + "}},"
            + "\"id\":1}";
    public static final String SESSION_JSON_NULL_USERNAME = "{\"auth_token\":{"
            + "\"auth_type\":\"oauth1a\","
            + "\"auth_token\":{"
            + "\"token\":\"token\","
            + "\"secret\":\"secret\","
            + "\"created_at\":" + CREATED_AT + "}},"
            + "\"id\":1}";
    private static final String SESSION_JSON_INVALID_OAUTH_TYPE =
            "{\"auth_token\":{\"auth_type\":\"INVALID\",\"auth_token\":{"
                    + "\"token\":\"token\","
                    + "\"secret\":\"secret\","
                    + "\"created_at\":" + CREATED_AT + "}},"
                    + "\"id\":1}";

    private DigitsSession.Serializer serializer;

    @Before
    public void setUp() throws Exception {

        serializer = new DigitsSession.Serializer();
    }

    @Test
    public void testDeserialize_sessionWithAuthToken() throws Exception {
        final DigitsSession session = serializer.deserialize(SESSION_JSON);
        final DigitsSession newSession = new DigitsSession(
                AuthTokenUtils.createTwitterAuthToken(TestConstants.TOKEN, TestConstants.SECRET,
                  CREATED_AT),
                DigitsSession.UNKNOWN_USER_ID);
        assertEquals(session, newSession);
    }

    @Test
    public void testDeserialize_session() throws Exception {
        final DigitsSession session = serializer.deserialize(FULL_SESSION_JSON);
        assertEquals(new DigitsSession(AuthTokenUtils.createTwitterAuthToken(
                TestConstants.TOKEN, TestConstants.SECRET,
                CREATED_AT), 1), session);
    }

    @Test
    public void testDeserialize_sessionWithNullUserName() throws Exception {
        final DigitsSession session = serializer.deserialize(SESSION_JSON_NULL_USERNAME);
        assertEquals(new DigitsSession(AuthTokenUtils.createTwitterAuthToken(
                TestConstants.TOKEN, TestConstants.SECRET,
                CREATED_AT), 1), session);
    }

    @Test
    public void testDeserialize_nullSerializedSession() throws Exception {
        final DigitsSession session = serializer.deserialize(null);
        assertNull(session);
    }

    @Test
    public void testDeserialize_invalidOAuthType() {
        final DigitsSession session = serializer.deserialize(SESSION_JSON_INVALID_OAUTH_TYPE);
        assertNull(session);
    }

    @Test
    public void testSerialize_sessionWithAuthToken() throws Exception {
        final DigitsSession session = new DigitsSession(
                AuthTokenUtils.createTwitterAuthToken(TestConstants.TOKEN, TestConstants.SECRET,
                  CREATED_AT),
                DigitsSession.UNKNOWN_USER_ID);
        assertEquals(SESSION_JSON, serializer.serialize(session));
    }

    @Test
    public void testSerialize_session() throws Exception {
        final DigitsSession session = new DigitsSession(AuthTokenUtils.createTwitterAuthToken(
          TestConstants.TOKEN, TestConstants.SECRET, CREATED_AT), 1);
        assertEquals(FULL_SESSION_JSON, serializer.serialize(session));
    }

    @Test
    public void testSerialize_sessionWithNullUserName() throws Exception {
        final DigitsSession session = new DigitsSession(AuthTokenUtils.createTwitterAuthToken(
          TestConstants.TOKEN, TestConstants.SECRET, CREATED_AT), 1);
        assertEquals(SESSION_JSON_NULL_USERNAME, serializer.serialize(session));
    }
}
