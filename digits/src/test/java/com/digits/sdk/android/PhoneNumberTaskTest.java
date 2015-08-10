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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class PhoneNumberTaskTest {
    private static final String EMPTY_PHONE = "";
    private PhoneNumberUtils phoneNumberUtils;
    private PhoneNumber phoneNumber;
    private PhoneNumberTask.Listener listener;
    private PhoneNumberTask taskWithProvidedPhone;
    private PhoneNumberTask task;

    @Before
    public void setUp() throws Exception {

        phoneNumberUtils = mock(PhoneNumberUtils.class);
        listener = mock(PhoneNumberTask.Listener.class);
        phoneNumber = new PhoneNumber(TestConstants.PHONE, TestConstants.US_ISO2,
                TestConstants.US_COUNTRY_CODE);
        taskWithProvidedPhone = new PhoneNumberTask(phoneNumberUtils, TestConstants.PHONE,
                listener);
        task = new PhoneNumberTask(phoneNumberUtils, listener);
    }

    @Test
    public void testDoInBackgroundProvidedPhone() throws Exception {
        taskWithProvidedPhone.doInBackground();
        verify(phoneNumberUtils).getPhoneNumber(TestConstants.PHONE);
    }

    @Test
    public void testDoInBackground() throws Exception {
        task.doInBackground();
        verify(phoneNumberUtils).getPhoneNumber(EMPTY_PHONE);
    }

    @Test
    public void testOnPostExecute() throws Exception {
        task.onPostExecute(phoneNumber);
        verify(listener).onLoadComplete(phoneNumber);
    }

    @Test
    public void testOnPostExecuteProvidedPhone() throws Exception {
        taskWithProvidedPhone.onPostExecute(phoneNumber);
        verify(listener).onLoadComplete(phoneNumber);
    }

    @Test
    public void testConstructor_nullPhoneNumberManager() throws Exception {
        try {
            new PhoneNumberTask(null, mock(PhoneNumberTask.Listener.class));
        } catch (NullPointerException ex) {
            assertEquals("phoneNumberUtils can't be null", ex.getMessage());
        }
    }

    @Test
    public void testConstructor_nullListener() throws Exception {
        new PhoneNumberTask(phoneNumberUtils, null);
    }
}
