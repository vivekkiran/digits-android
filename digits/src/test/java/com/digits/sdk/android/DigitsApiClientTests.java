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

import com.twitter.sdk.android.core.TwitterAuthConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.concurrent.ExecutorService;

import javax.net.ssl.SSLSocketFactory;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class DigitsApiClientTests {
    private TwitterAuthConfig authConfig;
    private DigitsSession guestSession;
    private DigitsApiClient digitsApiClient;

    @Before
    public void setUp() throws Exception {
        authConfig = new TwitterAuthConfig(TestConstants.CONSUMER_SECRET,
                TestConstants.CONSUMER_KEY);
        guestSession = DigitsSession.create(DigitsSessionTests.getNewLoggedOutUser(),
                TestConstants.PHONE);

        digitsApiClient = new DigitsApiClient(guestSession, authConfig,
                mock(SSLSocketFactory.class), mock(ExecutorService.class),
                mock(DigitsUserAgent.class));
    }

    @Test
    public void testGetSdkService() throws Exception {
        final DigitsApiClient.SdkService sdkService = digitsApiClient.getSdkService();
        final DigitsApiClient.SdkService newSdkService = digitsApiClient.getSdkService();
        assertTrue(sdkService == newSdkService);
    }

    @Test
    public void testGetDeviceService() throws Exception {
        final DigitsApiClient.DeviceService deviceService = digitsApiClient.getDeviceService();
        final DigitsApiClient.DeviceService newDeviceService = digitsApiClient.getDeviceService();
        assertTrue(deviceService == newDeviceService);
    }

    @Test
    public void testGetAccountService() throws Exception {
        final DigitsApiClient.AccountService accountService = digitsApiClient.getAccountService();
        final DigitsApiClient.AccountService newAccountService = digitsApiClient
                .getAccountService();
        assertTrue(accountService == newAccountService);
    }
}

