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
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.twitter.sdk.android.core.TwitterApiErrorConstants;

import org.mockito.ArgumentCaptor;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FailureActivityDelegateImplTests extends DigitsAndroidTestCase {

    Activity activity;
    FailureController controller;
    FailureActivityDelegateImpl delegate;
    ArgumentCaptor<View.OnClickListener> captorClick;
    Intent intent;
    Button button;
    TextView textView;
    Bundle bundle;
    DigitsException exception;
    ResultReceiver resultReceiver;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        activity = mock(Activity.class);
        controller = mock(FailureController.class);
        delegate = spy(new DummyFailureDelegateImpl(activity, controller));
        captorClick = ArgumentCaptor.forClass(View.OnClickListener.class);
        intent = mock(Intent.class);
        button = mock(Button.class);
        textView = mock(TextView.class);
        bundle = new Bundle();
        resultReceiver = new ResultReceiver(null);
        exception = new DigitsException("", TwitterApiErrorConstants.UNKNOWN_ERROR);

        bundle.putParcelable(DigitsClient.EXTRA_RESULT_RECEIVER, resultReceiver);
        bundle.putSerializable(DigitsClient.EXTRA_FALLBACK_REASON, exception);
        when(intent.getExtras()).thenReturn(bundle);
        when(activity.getIntent()).thenReturn(intent);
    }

    public void testInit_validBundle() {
        when(activity.findViewById(R.id.dgts__dismiss_button)).thenReturn(button);
        when(activity.findViewById(R.id.dgts__try_another_phone)).thenReturn(button);

        delegate.init();

        verify(delegate).setContentView();
        verify(delegate).setUpViews();
    }

    public void testInit_invalidBundle() {
        when(intent.getExtras()).thenReturn(null);

        try {
            delegate.init();
            fail("Expected IllegalAccessError to be thrown");
        } catch (IllegalAccessError e) {
            assertEquals("This activity can only be started from Digits", e.getMessage());
        }
    }

    public void testSetContentView() {
        delegate.setContentView();

        verify(activity).setContentView(R.layout.dgts__activity_failure);
    }

    public void testSetUpViews() {
        when(activity.findViewById(R.id.dgts__dismiss_button)).thenReturn(button);
        when(activity.findViewById(R.id.dgts__try_another_phone)).thenReturn(button);

        delegate.setUpViews();

        verify(delegate).setUpDismissButton(button);
        verify(delegate).setUpTryAnotherPhoneButton(button);
    }

    public void testSetUpDismissButton() {
        delegate.setUpDismissButton(button);

        verify(button).setOnClickListener(captorClick.capture());
        final View.OnClickListener listener = captorClick.getValue();
        listener.onClick(null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            verify(activity).finishAffinity();
        } else {
            verify(activity).setResult(anyInt());
            verify(activity).finish();
        }
        verify(controller).sendFailure(resultReceiver, exception);
    }

    public void testSetUpTryAnotherPhoneButton() {
        delegate.setUpTryAnotherPhoneButton(button);

        verify(button).setOnClickListener(captorClick.capture());
        final View.OnClickListener listener = captorClick.getValue();
        listener.onClick(null);
        verify(controller).tryAnotherNumber(eq(activity), any(ResultReceiver.class));
        verify(activity).finish();
    }

    public class DummyFailureDelegateImpl extends FailureActivityDelegateImpl {

        public DummyFailureDelegateImpl(Activity activity, FailureController controller) {
            super(activity, controller);
        }
    }
}
