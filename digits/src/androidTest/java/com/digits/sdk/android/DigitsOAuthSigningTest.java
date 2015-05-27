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

import io.fabric.sdk.android.services.network.HttpMethod;

import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.internal.oauth.OAuth1aHeaders;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DigitsOAuthSigningTest extends DigitsAndroidTestCase {
    private static final String ANY_AUTH_HEADER = "Digits Authority!";

    public void testGetOAuthEchoHeadersForVerifyCredentials() throws Exception {
        final OAuth1aHeaders oAuthHeaders = mock(OAuth1aHeaders.class);
        final TwitterAuthConfig config = mock(TwitterAuthConfig.class);
        final TwitterAuthToken token = mock(TwitterAuthToken.class);
        when(oAuthHeaders.getAuthorizationHeader(config, token, null, HttpMethod.GET.name(),
                DigitsOAuthSigning.VERIFY_CREDENTIALS_URL, null)).thenReturn(ANY_AUTH_HEADER);

        final DigitsOAuthSigning oAuthSigning = new DigitsOAuthSigning(config, token, oAuthHeaders);
        oAuthSigning.getOAuthEchoHeadersForVerifyCredentials();

        verify(oAuthHeaders).getOAuthEchoHeaders(config, token, null, HttpMethod.GET.name(),
                DigitsOAuthSigning.VERIFY_CREDENTIALS_URL, null);
    }
}
