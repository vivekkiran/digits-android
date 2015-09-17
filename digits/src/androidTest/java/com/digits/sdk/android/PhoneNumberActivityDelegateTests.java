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
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.SpannedString;
import android.view.View;
import android.widget.EditText;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class PhoneNumberActivityDelegateTests extends
        DigitsActivityDelegateTests<PhoneNumberActivityDelegate> {
    CountryListSpinner spinner;

    @Override
    public void setUp() throws Exception {
        spinner = mock(CountryListSpinner.class);
        super.setUp();
    }

    @Override
    public PhoneNumberActivityDelegate getDelegate() {
        return spy(new DummyPhoneNumberActivityDelegate(scribeService));
    }

    public void testIsValid() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(DigitsClient.EXTRA_RESULT_RECEIVER, new ResultReceiver(null));

        assertTrue(delegate.isValid(bundle));
    }

    public void testIsValid_missingResultReceiver() {
        final Bundle bundle = new Bundle();

        assertFalse(delegate.isValid(bundle));
    }

    public void testGetLayoutId() {
        assertEquals(R.layout.dgts__activity_phone_number, delegate.getLayoutId());
    }

    public void testSetUpCountrySpinner() {
        final PhoneNumberController controller = mock(DummyPhoneNumberController.class);
        delegate.controller = controller;
        delegate.setUpCountrySpinner(spinner);

        verify(spinner).setOnClickListener(captorClick.capture());
        final View.OnClickListener listener = captorClick.getValue();
        listener.onClick(null);
        verify(controller).clearError();
        verify(scribeService).click(DigitsScribeConstants.Element.COUNTRY_CODE);
    }

    public void testOnResume() {
        final PhoneNumberController controller = mock(DummyPhoneNumberController.class);
        delegate.controller = controller;
        delegate.onResume();
        verify(controller).onResume();
        verify(scribeService).impression();
    }

    @Override
    public void testSetUpTermsText() throws Exception {
        doReturn(new SpannedString("")).when(delegate).getFormattedTerms(any(Activity.class),
                anyInt());
        delegate.setUpTermsText(activity, controller, textView);
        verify(delegate).getFormattedTerms(activity, R.string.dgts__terms_text);
        verify(textView).setText(new SpannedString(""));
    }

    public void testOnLoadComplete() {
        final PhoneNumberController controller = mock(DummyPhoneNumberController.class);
        delegate.controller = controller;
        delegate.onLoadComplete(PhoneNumber.emptyPhone());
        verify(controller).setPhoneNumber(PhoneNumber.emptyPhone());
        verify(controller).setCountryCode(PhoneNumber.emptyPhone());
    }

    public void testOnActivityResult_resendResult() throws Exception {
        final PhoneNumberController controller = mock(DummyPhoneNumberController.class);
        delegate.controller = controller;
        delegate.onActivityResult(DigitsActivity.REQUEST_CODE,
                DigitsActivity.RESULT_RESEND_CONFIRMATION, activity);
        verify(controller).resend();
    }

    public void testOnActivityResult_notResendResult() throws Exception {
        final PhoneNumberController controller = mock(DummyPhoneNumberController.class);
        delegate.controller = controller;
        delegate.onActivityResult(ANY_REQUEST, ANY_RESULT, activity);
        verifyNoInteractions(controller);
    }

    public class DummyPhoneNumberActivityDelegate extends PhoneNumberActivityDelegate {

        public DummyPhoneNumberActivityDelegate(DigitsScribeService scribeService) {
            super(scribeService);
        }
    }

    public class DummyPhoneNumberController extends PhoneNumberController {

        DummyPhoneNumberController(ResultReceiver resultReceiver, StateButton stateButton,
                                   EditText phoneEditText, CountryListSpinner countryCodeSpinner,
                                   TosView tosView, DigitsScribeService scribeService,
                                   boolean emailCollection) {
            super(resultReceiver, stateButton, phoneEditText, countryCodeSpinner,
                    tosView, scribeService, emailCollection);
        }
    }
}
