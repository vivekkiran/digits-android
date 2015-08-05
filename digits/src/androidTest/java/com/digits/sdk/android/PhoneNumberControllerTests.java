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

import android.content.Intent;
import android.text.Editable;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;

import java.util.Locale;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PhoneNumberControllerTests extends DigitsControllerTests<PhoneNumberController> {
    private CountryListSpinner countrySpinner;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        countrySpinner = mock(CountryListSpinner.class);
        controller = new PhoneNumberController(resultReceiver, sendButton, phoneEditText,
                countrySpinner, digitsClient, errors, new ActivityClassManagerImp(),
                sessionManager);
        when(countrySpinner.getTag()).thenReturn(COUNTRY_CODE);
    }

    public void testExecuteRequest_success() throws Exception {
        when(errors.getDefaultMessage()).thenReturn(ERROR_MESSAGE);
        final Callback callback = executeRequest();
        callback.success(null, null);
        verify(phoneEditText).postDelayed(any(Runnable.class),
                eq(PhoneNumberController.POST_DELAY_MS));
        verify(sendButton).showFinish();
    }

    DigitsCallback executeRequest() {
        when(phoneEditText.getText()).thenReturn(Editable.Factory.getInstance().newEditable
                (PHONE));

        when(countrySpinner.getTag()).thenReturn(Integer.valueOf(COUNTRY_CODE));
        controller.executeRequest(context);
        verify(sendButton).showProgress();

        verify(digitsClient).authDevice(eq(PHONE_WITH_COUNTRY_CODE), callbackCaptor.capture());
        assertNotNull(callbackCaptor.getValue());
        assertEquals(PHONE_WITH_COUNTRY_CODE, controller.phoneNumber);
        return callbackCaptor.getValue();
    }


    public void testHandleError_couldNotAuthenticateException() throws Exception {
        final DeviceRegistrationResponse data = new DeviceRegistrationResponse();
        data.normalizedPhoneNumber = PHONE_WITH_COUNTRY_CODE;
        final Intent intent = handleErrorSuccess(data);
        assertEquals(ConfirmationCodeActivity.class.getName(),
                intent.getComponent().getClassName());
        assertEquals(resultReceiver, intent.getExtras().get(DigitsClient.EXTRA_RESULT_RECEIVER));
        assertEquals(data.normalizedPhoneNumber, intent.getExtras().get(DigitsClient.EXTRA_PHONE));
    }

    public void testHandleError_couldNotAuthenticateExceptionNullData() throws Exception {
        final Intent intent = handleErrorSuccess(new DeviceRegistrationResponse());
        assertEquals(ConfirmationCodeActivity.class.getName(),
                intent.getComponent().getClassName());
        assertEquals(resultReceiver, intent.getExtras().get(DigitsClient.EXTRA_RESULT_RECEIVER));
        assertEquals(PHONE, intent.getExtras().get(DigitsClient.EXTRA_PHONE));
    }

    private Intent handleErrorSuccess(DeviceRegistrationResponse data) {
        controller.phoneNumber = PHONE;
        controller.handleError(context, new CouldNotAuthenticateException(ERROR_MESSAGE));
        verify(digitsClient).registerDevice(eq(PHONE), callbackCaptor.capture());
        final Callback<DeviceRegistrationResponse> callback = callbackCaptor.getValue();
        final Result<DeviceRegistrationResponse> deviceResponse = new Result<>(data, null);
        callback.success(deviceResponse);
        verify(context).startActivityForResult(intentCaptor.capture(),
                eq(DigitsActivity.REQUEST_CODE));
        verify(sendButton).showFinish();
        return intentCaptor.getValue();
    }

    public void testStartSignIn() {
        controller.startSignIn(context, createAuthResponse());

        verify(context).startActivityForResult(intentCaptor.capture(),
                eq(DigitsActivity.REQUEST_CODE));
        final Intent intent = intentCaptor.getValue();
        assertEquals(REQUEST_ID, intent.getStringExtra(DigitsClient.EXTRA_REQUEST_ID));
        assertEquals(USER_ID, intent.getLongExtra(DigitsClient.EXTRA_USER_ID, 0));
        assertEquals(true,
                ((AuthConfig) intent.getParcelableExtra(DigitsClient.EXTRA_AUTH_CONFIG)).tosUpdate);
    }

    public void testValidateInput_valid() throws Exception {
        assertTrue(controller.validateInput(CODE));
    }

    public void testValidateInput_null() throws Exception {
        assertFalse(controller.validateInput(null));
    }

    public void testValidateInput_empty() throws Exception {
        assertFalse(controller.validateInput(EMPTY_CODE));
    }

    public void testSetPhoneNumber_validPhoneNumber() throws Exception {
        final PhoneNumber validPhoneNumber = new PhoneNumber(PHONE, US_ISO2, US_COUNTRY_CODE);
        controller.setPhoneNumber(validPhoneNumber);
        verify(phoneEditText).setText(validPhoneNumber.getPhoneNumber());
        verify(phoneEditText).setSelection(validPhoneNumber.getPhoneNumber().length());
    }

    public void testSetPhoneNumber_invalidPhoneNumber() throws Exception {
        controller.setPhoneNumber(PhoneNumber.emptyPhone());
        PhoneNumber invalidPhoneNumber = new PhoneNumber("", US_ISO2, US_COUNTRY_CODE);
        controller.setPhoneNumber(invalidPhoneNumber);
        invalidPhoneNumber = new PhoneNumber(PHONE, "", US_COUNTRY_CODE);
        controller.setPhoneNumber(invalidPhoneNumber);
        invalidPhoneNumber = new PhoneNumber(PHONE, US_ISO2, "");
        controller.setPhoneNumber(invalidPhoneNumber);
        verifyNoInteractions(phoneEditText);
    }

    public void testSetCountryCode_validPhoneNumber() throws Exception {
        final PhoneNumber validPhoneNumber = new PhoneNumber(PHONE, US_ISO2, US_COUNTRY_CODE);
        controller.setCountryCode(validPhoneNumber);
        verify(countrySpinner).setSelectedForCountry(new Locale("",
                        validPhoneNumber.getCountryIso()).getDisplayName(),
                validPhoneNumber.getCountryCode());
    }

    public void testSetCountryCode_validCountryNoPhoneNumber() throws Exception {
        final PhoneNumber validCountryNoPhoneNumber = new PhoneNumber("", US_ISO2, US_COUNTRY_CODE);
        controller.setCountryCode(validCountryNoPhoneNumber);
        verify(countrySpinner).setSelectedForCountry(new Locale("",
                        validCountryNoPhoneNumber.getCountryIso()).getDisplayName(),
                validCountryNoPhoneNumber.getCountryCode());
    }

    public void testSetCountryCode_invalidPhoneNumber() throws Exception {
        controller.setCountryCode(PhoneNumber.emptyPhone());
        PhoneNumber invalidPhoneNumber = new PhoneNumber(PHONE, "", US_COUNTRY_CODE);
        controller.setCountryCode(invalidPhoneNumber);
        invalidPhoneNumber = new PhoneNumber(PHONE, US_ISO2, "");
        controller.setCountryCode(invalidPhoneNumber);
        verifyNoInteractions(countrySpinner);
    }

    private AuthResponse createAuthResponse() {
        final AuthResponse authResponse = new AuthResponse();
        authResponse.requestId = REQUEST_ID;
        authResponse.userId = USER_ID;
        authResponse.authConfig = new AuthConfig();
        authResponse.authConfig.tosUpdate = true;

        return authResponse;
    }
}
