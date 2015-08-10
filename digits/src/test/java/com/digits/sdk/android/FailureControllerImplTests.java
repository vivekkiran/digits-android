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
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.ResultReceiver;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class FailureControllerImplTests {
    private static final String RANDOM_ERROR_MSG = "Random error message";
    FailureControllerImpl controller;
    ArgumentCaptor<Intent> intentArgumentCaptor;
    ArgumentCaptor<Bundle> bundleArgumentCaptor;
    Activity activity;
    ResultReceiver receiver;
    DigitsException exception;
    Class<?> activityClass;

    @Before
    public void setUp() throws Exception {
        activity = mock(Activity.class);
        intentArgumentCaptor = ArgumentCaptor.forClass(Intent.class);
        bundleArgumentCaptor = ArgumentCaptor.forClass(Bundle.class);
        receiver = mock(ResultReceiver.class);
        exception = mock(DigitsException.class);
        controller = new FailureControllerImpl(new ActivityClassManagerImp());
        activityClass = controller.classManager.getPhoneNumberActivity();

        when(activity.getPackageName()).thenReturn(activityClass.getPackage().toString());
    }

    @Test
    public void testTryAnotherNumber() {
        controller.tryAnotherNumber(activity, receiver);

        verify(activity).startActivity(intentArgumentCaptor.capture());
        final Intent intent = intentArgumentCaptor.getValue();
        verifyFlags(intent.getFlags());
        final Bundle bundle = intent.getExtras();
        assertTrue(BundleManager.assertContains(bundle, DigitsClient.EXTRA_RESULT_RECEIVER));
        final ComponentName activityComponent = new ComponentName(activity, activityClass);
        assertEquals(activityComponent, intent.getComponent());
    }

    @Test
    public void testSendFailure() {
        when(exception.getLocalizedMessage()).thenReturn(RANDOM_ERROR_MSG);

        controller.sendFailure(receiver, exception);

        verify(receiver).send(eq(LoginResultReceiver.RESULT_ERROR), bundleArgumentCaptor.capture());
        final Bundle bundle = bundleArgumentCaptor.getValue();
        assertEquals(RANDOM_ERROR_MSG, bundle.getString(LoginResultReceiver.KEY_ERROR));
    }

    @Test
    public void testFlags() {
        verifyFlags(controller.getFlags());
    }

    void verifyFlags(int actual) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            final int expected = Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK;
            assertEquals(expected, actual);
        } else {
            final int expected = Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK;
            assertEquals(expected, actual);
        }
    }
}
