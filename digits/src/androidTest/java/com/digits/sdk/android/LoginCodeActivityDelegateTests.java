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

import android.app.Activity;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.SpannedString;
import android.view.View;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class LoginCodeActivityDelegateTests extends
        DigitsActivityDelegateTests<LoginCodeActivityDelegate> {
    @Override
    public LoginCodeActivityDelegate getDelegate() {
        return spy(new DummyLoginCodeActivityDelegate(scribeService));
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
        assertEquals(R.layout.dgts__activity_confirmation, delegate.getLayoutId());
    }

    @Override
    public void testSetUpTermsText() throws Exception {
        delegate.config = new AuthConfig();
        delegate.config.tosUpdate = Boolean.FALSE;
        delegate.setUpTermsText(activity, controller, textView);

        verify(textView).setVisibility(View.GONE);
    }

    public void testSetUpTermsText_tosUpdated() throws Exception {
        doReturn(new SpannedString("")).when(delegate).getFormattedTerms(any(Activity.class),
                anyInt());
        delegate.config = new AuthConfig();
        delegate.config.tosUpdate = Boolean.TRUE;
        delegate.setUpTermsText(activity, controller, textView);

        verify(delegate).getFormattedTerms(any(Activity.class),
                eq(R.string.dgts__terms_text_sign_in));
    }

    public void testSetUpResendText() throws NoSuchFieldException, IllegalAccessException {
        delegate.controller = controller;
        delegate.setUpResendText(activity, editText);

        verify(editText).setOnClickListener(captorClick.capture());
        final View.OnClickListener listener = captorClick.getValue();
        listener.onClick(null);
        verify(scribeService).click(DigitsScribeConstants.Element.RESEND);
        verify(activity).finish();
        verifyResultCode(activity, DigitsActivity.RESULT_RESEND_CONFIRMATION);
    }

    public void testOnResume() {
        delegate.controller = controller;
        delegate.onResume();
        verify(controller).onResume();
        verify(scribeService).impression();
    }

    public void testSetUpSmsIntercept_permissionDenied() {
        when(activity.checkCallingOrSelfPermission("android.permission.RECEIVE_SMS"))
                .thenReturn(PackageManager.PERMISSION_DENIED);

        delegate.setUpSmsIntercept(activity, editText);

        verify(activity).checkCallingOrSelfPermission("android.permission.RECEIVE_SMS");
        verifyNoMoreInteractions(activity);
    }

    public void testSetUpSmsIntercept_permissionGranted() {
        when(activity.checkCallingOrSelfPermission("android.permission.RECEIVE_SMS"))
                .thenReturn(PackageManager.PERMISSION_GRANTED);

        delegate.setUpSmsIntercept(activity, editText);

        verify(activity).checkCallingOrSelfPermission("android.permission.RECEIVE_SMS");
        verify(activity).registerReceiver(any(SmsBroadcastReceiver.class), any(IntentFilter.class));
    }

    public class DummyLoginCodeActivityDelegate extends LoginCodeActivityDelegate {

        DummyLoginCodeActivityDelegate(DigitsScribeService scribeService) {
            super(scribeService);
        }
    }
}
