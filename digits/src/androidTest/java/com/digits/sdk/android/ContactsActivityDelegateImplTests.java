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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.mockito.ArgumentCaptor;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ContactsActivityDelegateImplTests extends DigitsAndroidTestCase {

    Activity activity;
    ContactsController controller;
    ContactsActivityDelegateImpl delegate;
    ArgumentCaptor<View.OnClickListener> captorClick;
    Button button;
    TextView textView;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        activity = mock(Activity.class);
        controller = mock(ContactsController.class);
        delegate = spy(new DummyContactsDelegateImpl(activity, controller));
        captorClick = ArgumentCaptor.forClass(View.OnClickListener.class);
        button = mock(Button.class);
        textView = mock(TextView.class);
    }

    public void testInit() {
        when(activity.findViewById(R.id.dgts__not_now)).thenReturn(button);
        when(activity.findViewById(R.id.dgts__okay)).thenReturn(button);
        when(activity.findViewById(R.id.dgts__upload_contacts)).thenReturn(textView);

        delegate.init();

        verify(delegate).setContentView();
        verify(delegate).setUpViews();
    }

    public void testSetContentView() {
        delegate.setContentView();

        verify(activity).setContentView(R.layout.dgts__activity_contacts);
    }

    public void testSetUpViews() {
        when(activity.findViewById(R.id.dgts__not_now)).thenReturn(button);
        when(activity.findViewById(R.id.dgts__okay)).thenReturn(button);
        when(activity.findViewById(R.id.dgts__upload_contacts)).thenReturn(textView);

        delegate.setUpViews();

        verify(delegate).setUpOkayButton(button);
        verify(delegate).setUpNotNowButton(button);
        verify(delegate).setUpDescription(textView);
    }

    public void testSetUpNotNowButton() {
        delegate.setUpNotNowButton(button);

        verify(button).setOnClickListener(captorClick.capture());
        final View.OnClickListener listener = captorClick.getValue();
        listener.onClick(null);
        verify(activity).finish();
    }

    public void testSetUpOkayButton() {
        delegate.setUpOkayButton(button);

        verify(button).setOnClickListener(captorClick.capture());
        final View.OnClickListener listener = captorClick.getValue();
        listener.onClick(null);
        verify(controller).startUploadService(activity);
        verify(activity).finish();
    }

    public class DummyContactsDelegateImpl extends ContactsActivityDelegateImpl {

        public DummyContactsDelegateImpl(Activity activity, ContactsController controller) {
            super(activity, controller);
        }

        protected String getFormattedDescription() {
            return "";
        }
    }
}
