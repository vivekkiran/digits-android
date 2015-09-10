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

import android.os.Bundle;
import android.os.ResultReceiver;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class PinCodeActivityDelegateTests extends
        DigitsActivityDelegateTests<PinCodeActivityDelegate> {

    @Override
    public PinCodeActivityDelegate getDelegate() {
        return spy(new DummyPinCodeActivityDelegate(scribeService));
    }

    public void testIsValid() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(DigitsClient.EXTRA_RESULT_RECEIVER, new ResultReceiver(null));
        bundle.putString(DigitsClient.EXTRA_PHONE, "");
        bundle.putString(DigitsClient.EXTRA_REQUEST_ID, "");
        bundle.putString(DigitsClient.EXTRA_USER_ID, "");

        assertTrue(delegate.isValid(bundle));
    }

    public void testIsValid_missingResultReceiver() {
        final Bundle bundle = new Bundle();
        bundle.putString(DigitsClient.EXTRA_PHONE, "");
        bundle.putString(DigitsClient.EXTRA_REQUEST_ID, "");
        bundle.putString(DigitsClient.EXTRA_USER_ID, "");

        assertFalse(delegate.isValid(bundle));
    }

    public void testIsValid_missingPhoneNumber() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(DigitsClient.EXTRA_RESULT_RECEIVER, new ResultReceiver(null));
        bundle.putString(DigitsClient.EXTRA_REQUEST_ID, "");
        bundle.putString(DigitsClient.EXTRA_USER_ID, "");

        assertFalse(delegate.isValid(bundle));
    }

    public void testIsValid_missingRequestId() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(DigitsClient.EXTRA_RESULT_RECEIVER, new ResultReceiver(null));
        bundle.putString(DigitsClient.EXTRA_PHONE, "");
        bundle.putString(DigitsClient.EXTRA_USER_ID, "");

        assertFalse(delegate.isValid(bundle));
    }

    public void testIsValid_missingUserId() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(DigitsClient.EXTRA_RESULT_RECEIVER, new ResultReceiver(null));
        bundle.putString(DigitsClient.EXTRA_PHONE, "");
        bundle.putString(DigitsClient.EXTRA_REQUEST_ID, "");

        assertFalse(delegate.isValid(bundle));
    }

    public void testGetLayoutId() {
        assertEquals(R.layout.dgts__activity_pin_code, delegate.getLayoutId());
    }

    public void testOnResume() {
        delegate.controller = controller;
        delegate.onResume();
        verify(controller).onResume();
        verify(scribeService).impression();
    }

    public class DummyPinCodeActivityDelegate extends PinCodeActivityDelegate {

        DummyPinCodeActivityDelegate(DigitsScribeService scribeService) {
            super(scribeService);
        }
    }
}
