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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 21)
public class PhoneNumberTaskTest {
    private PhoneNumberUtils phoneNumberUtils;
    private PhoneNumber phoneNumber;

    @Before
    public void setUp() throws Exception {

        phoneNumberUtils = mock(PhoneNumberUtilsTest.DummyPhoneNumberUtils.class);
        phoneNumber = new PhoneNumber(TestConstants.PHONE, TestConstants.US_ISO2,
                TestConstants.US_COUNTRY_CODE);
        when(phoneNumberUtils.getPhoneNumber()).thenReturn(phoneNumber);
    }

    @Test
    public void testExecute() throws Exception {

        new PhoneNumberTask(phoneNumberUtils, new PhoneNumberTask.Listener() {
            @Override
            public void onLoadComplete(PhoneNumber result) {
                assertEquals(phoneNumber, result);
                verify(phoneNumberUtils).getPhoneNumber();
            }
        }).execute();
    }

    @Test
    public void testConstructor_nullPhoneNumberManager() throws Exception {
        try {
            new PhoneNumberTask(null, mock(PhoneNumberTask.Listener.class));
        } catch (NullPointerException ex) {
            assertEquals("phoneNumberManager can't be null", ex.getMessage());
        }
    }

    @Test
    public void testConstructor_nullListener() throws Exception {
        new PhoneNumberTask(phoneNumberUtils, null);
    }
}
