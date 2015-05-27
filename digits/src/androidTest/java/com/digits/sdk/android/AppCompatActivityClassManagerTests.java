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

import junit.framework.TestCase;

public class AppCompatActivityClassManagerTests extends TestCase {
    private AppCompatClassManagerImp activityClassManager;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        activityClassManager = new AppCompatClassManagerImp();
    }

    public void testGetPhoneNumberActivity() throws Exception {
        assertEquals(PhoneNumberActionBarActivity.class,
                activityClassManager.getPhoneNumberActivity());
    }

    public void testGetConfirmationActivity() throws Exception {
        assertEquals(ConfirmationCodeActionBarActivity.class,
                activityClassManager.getConfirmationActivity());
    }

    public void testGetLoginCodeActivity() throws Exception {
        assertEquals(LoginCodeActionBarActivity.class, activityClassManager.getLoginCodeActivity());
    }

    public void testFailureActivity() throws Exception {
        assertEquals(FailureActionBarActivity.class,
                activityClassManager.getFailureActivity());
    }

    public void testContactsActivity() throws Exception {
        assertEquals(ContactsActionBarActivity.class, activityClassManager.getContactsActivity());
    }

    public void testPinCodeActivity() throws Exception {
        assertEquals(PinCodeActionBarActivity.class,
                activityClassManager.getPinCodeActivity());
    }
}
