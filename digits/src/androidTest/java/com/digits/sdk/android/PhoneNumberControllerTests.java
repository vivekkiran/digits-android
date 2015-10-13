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
import com.twitter.sdk.android.core.TwitterApiErrorConstants;

import java.util.Locale;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PhoneNumberControllerTests extends DigitsControllerTests<PhoneNumberController> {
    private CountryListSpinner countrySpinner;
    private Verification verification;
    private TosView tosView;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        verification = Verification.sms;
        countrySpinner = mock(CountryListSpinner.class);
        tosView = mock(TosView.class);
        controller = new PhoneNumberController(resultReceiver,
                sendButton, phoneEditText, countrySpinner, digitsClient, errors,
                new ActivityClassManagerImp(), sessionManager, tosView, scribeService, false);
        assertFalse(controller.voiceEnabled);
        assertFalse(controller.emailCollection);
        assertFalse(controller.resendState);
        when(countrySpinner.getTag()).thenReturn(COUNTRY_CODE);
    }

    public void testExecuteRequest_successSmsVerification() throws Exception {
        when(errors.getDefaultMessage()).thenReturn(ERROR_MESSAGE);

        final DigitsCallback<AuthResponse> callback = executeRequest();
        callback.success(createAuthResponse(), null);

        assertTrue(controller.voiceEnabled);
        assertFalse(controller.emailCollection);
        verify(phoneEditText).postDelayed(any(Runnable.class),
                eq(PhoneNumberController.POST_DELAY_MS));
        verify(sendButton).showFinish();
    }

    public void testExecuteRequest_successMailEnabled() throws Exception {
        controller.emailCollection = true;

        final DigitsCallback<AuthResponse> callback = executeRequest();
        callback.success(createAuthResponse(), null);

        assertTrue(controller.emailCollection);
    }

    public void testExecuteRequest_successMailDisabled() throws Exception {
        controller.emailCollection = true;

        final DigitsCallback<AuthResponse> callback = executeRequest();
        callback.success(createAuthResponseDisabledEmail(), null);

        assertFalse(controller.emailCollection);
    }

    public void testExecuteRequest_successVoiceVerification() throws Exception {
        controller.voiceEnabled = true;
        controller.resend();
        verification = Verification.voicecall;
        when(errors.getDefaultMessage()).thenReturn(ERROR_MESSAGE);

        final DigitsCallback<AuthResponse> callback = executeRequest();
        callback.success(createAuthResponse(), null);

        assertTrue(controller.voiceEnabled);
        assertFalse(controller.emailCollection);
        verify(phoneEditText).postDelayed(any(Runnable.class),
                eq(PhoneNumberController.POST_DELAY_MS));
        verify(sendButton).showFinish();
    }

    public void testExecuteRequest_successResendWithVoiceVerificationDisabled() throws Exception {
        controller.resend();
        when(errors.getDefaultMessage()).thenReturn(ERROR_MESSAGE);

        final DigitsCallback<AuthResponse> callback = executeRequest();
        callback.success(createAuthResponse(), null);

        assertTrue(controller.voiceEnabled);
        assertFalse(controller.emailCollection);
        verify(phoneEditText).postDelayed(any(Runnable.class),
                eq(PhoneNumberController.POST_DELAY_MS));
        verify(sendButton).showFinish();
    }

    @Override
    DigitsCallback<AuthResponse> executeRequest() {
        when(phoneEditText.getText()).thenReturn(Editable.Factory.getInstance().newEditable
                (PHONE));
        when(countrySpinner.getTag()).thenReturn(Integer.valueOf(COUNTRY_CODE));

        controller.executeRequest(context);
        verify(scribeService).click(DigitsScribeConstants.Element.SUBMIT);
        verify(sendButton).showProgress();
        verify(digitsClient).authDevice(eq(PHONE_WITH_COUNTRY_CODE), eq(getVerification())
                , callbackCaptor.capture());
        assertNotNull(callbackCaptor.getValue());
        assertEquals(PHONE_WITH_COUNTRY_CODE, controller.phoneNumber);
        return callbackCaptor.getValue();
    }


    public void testHandleError_couldNotAuthenticateException() throws Exception {
        final DeviceRegistrationResponse data = new DeviceRegistrationResponse();
        data.normalizedPhoneNumber = PHONE_WITH_COUNTRY_CODE;
        data.authConfig = createAuthConfig(true, true, true);
        final Intent intent = handleErrorSuccess(data);
        assertTrue(controller.voiceEnabled);
        assertEquals(ConfirmationCodeActivity.class.getName(),
                intent.getComponent().getClassName());
        assertEquals(resultReceiver, intent.getExtras().get(DigitsClient.EXTRA_RESULT_RECEIVER));
        assertEquals(data.normalizedPhoneNumber, intent.getExtras().get(DigitsClient.EXTRA_PHONE));
        assertEquals(data.authConfig, intent.getParcelableExtra(DigitsClient.EXTRA_AUTH_CONFIG));
        assertEquals(controller.emailCollection, intent.getBooleanExtra(DigitsClient.EXTRA_EMAIL,
                false));
    }

    public void testHandleError_couldNotAuthenticateExceptionEnabledEmail() throws Exception {
        controller.emailCollection = true;
        final DeviceRegistrationResponse data = new DeviceRegistrationResponse();
        data.normalizedPhoneNumber = PHONE_WITH_COUNTRY_CODE;
        data.authConfig = createAuthConfig(true, true, true);
        final Intent intent = handleErrorSuccess(data);
        assertTrue(controller.emailCollection);
        assertEquals(ConfirmationCodeActivity.class.getName(),
                intent.getComponent().getClassName());
        assertEquals(resultReceiver, intent.getExtras().get(DigitsClient.EXTRA_RESULT_RECEIVER));
        assertEquals(data.normalizedPhoneNumber, intent.getExtras().get(DigitsClient.EXTRA_PHONE));
        assertEquals(data.authConfig, intent.getParcelableExtra(DigitsClient.EXTRA_AUTH_CONFIG));
        assertEquals(controller.emailCollection, intent.getBooleanExtra(DigitsClient.EXTRA_EMAIL,
                false));
    }

    public void testHandleError_couldNotAuthenticateExceptionDisabledEmail() throws Exception {
        controller.emailCollection = true;
        final DeviceRegistrationResponse data = new DeviceRegistrationResponse();
        data.normalizedPhoneNumber = PHONE_WITH_COUNTRY_CODE;
        data.authConfig = createAuthConfig(true, true, false);
        final Intent intent = handleErrorSuccess(data);
        assertFalse(controller.emailCollection);
        assertEquals(ConfirmationCodeActivity.class.getName(),
                intent.getComponent().getClassName());
        assertEquals(resultReceiver, intent.getExtras().get(DigitsClient.EXTRA_RESULT_RECEIVER));
        assertEquals(data.normalizedPhoneNumber, intent.getExtras().get(DigitsClient.EXTRA_PHONE));
        assertEquals(data.authConfig, intent.getParcelableExtra(DigitsClient.EXTRA_AUTH_CONFIG));
        assertEquals(controller.emailCollection, intent.getBooleanExtra(DigitsClient.EXTRA_EMAIL,
                false));
    }

    public void testHandleError_couldNotAuthenticateExceptionNullData() throws Exception {
        final Intent intent = handleErrorSuccess(new DeviceRegistrationResponse());
        assertFalse(controller.voiceEnabled);
        assertEquals(ConfirmationCodeActivity.class.getName(),
                intent.getComponent().getClassName());
        assertEquals(resultReceiver, intent.getExtras().get(DigitsClient.EXTRA_RESULT_RECEIVER));
        assertEquals(PHONE, intent.getExtras().get(DigitsClient.EXTRA_PHONE));
        assertNull(intent.getParcelableExtra(DigitsClient.EXTRA_AUTH_CONFIG));
        assertEquals(controller.emailCollection, intent.getBooleanExtra(DigitsClient.EXTRA_EMAIL,
                false));
    }

    private Intent handleErrorSuccess(DeviceRegistrationResponse data) {
        controller.phoneNumber = PHONE;
        controller.handleError(context, new CouldNotAuthenticateException(ERROR_MESSAGE));

        verify(digitsClient).registerDevice(eq(PHONE), eq(Verification.sms),
                callbackCaptor.capture());
        final Callback<DeviceRegistrationResponse> callback = callbackCaptor.getValue();
        final Result<DeviceRegistrationResponse> deviceResponse = new Result<>(data, null);

        callback.success(deviceResponse);
        verify(scribeService).success();
        verify(context).startActivityForResult(intentCaptor.capture(),
                eq(DigitsActivity.REQUEST_CODE));
        verify(sendButton).showFinish();
        return intentCaptor.getValue();
    }

    public void testStartSignIn() {
        controller.startSignIn(context, createAuthResponse());
        verify(scribeService).success();
        verify(context).startActivityForResult(intentCaptor.capture(),
                eq(DigitsActivity.REQUEST_CODE));
        final Intent intent = intentCaptor.getValue();
        assertEquals(REQUEST_ID, intent.getStringExtra(DigitsClient.EXTRA_REQUEST_ID));
        assertEquals(USER_ID, intent.getLongExtra(DigitsClient.EXTRA_USER_ID, 0));
        assertEquals(true,
                ((AuthConfig) intent.getParcelableExtra(DigitsClient.EXTRA_AUTH_CONFIG)).tosUpdate);
        assertEquals(controller.emailCollection, intent.getBooleanExtra(DigitsClient.EXTRA_EMAIL,
                false));
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

    public void testOnTextChanged_withVoiceVerification() throws Exception {
        controller.resend();
        assertTrue(controller.resendState);
        controller.voiceEnabled = true;

        controller.onTextChanged(PHONE, 0, 0, 0);

        assertFalse(controller.resendState);
        verify(sendButton).setStatesText(R.string.dgts__continue,
                R.string.dgts__sending,
                R.string.dgts__done);
        verify(sendButton).showStart();
        verify(tosView).setText(R.string.dgts__terms_text);
    }

    public void testOnTextChanged_withSmsVerification() throws Exception {
        controller.onTextChanged(PHONE, 0, 0, 0);

        assertFalse(controller.resendState);
        assertFalse(controller.voiceEnabled);
        verifyNoInteractions(sendButton);
        verifyNoInteractions(tosView);
    }

    public void testResend_withVoiceEnabled() throws Exception {
        controller.voiceEnabled = true;

        controller.resend();

        assertResendWithVoiceEnabled();
    }

    private void assertResendWithVoiceEnabled() {
        assertTrue(controller.resendState);
        verify(sendButton).setStatesText(R.string.dgts__call_me, R.string.dgts__calling,
                R.string.dgts__calling);
        verify(tosView).setText(R.string.dgts__terms_text_call_me);
    }

    public void testResend_withVoiceDisabled() throws Exception {
        controller.resend();

        assertTrue(controller.resendState);
        verifyNoInteractions(sendButton);
        verifyNoInteractions(tosView);
    }

    public void testHandleError_operatorUnsupportedWithVoiceEnabled() throws Exception {
        assertFalse(controller.voiceEnabled);
        controller.handleError(context, new OperatorUnsupportedException(ERROR_MESSAGE,
                TwitterApiErrorConstants.OPERATOR_UNSUPPORTED, createAuthConfig(true, true,
                true)));
        assertTrue(controller.voiceEnabled);
        assertResendWithVoiceEnabled();
        verify(phoneEditText).setError(ERROR_MESSAGE);
        verify(sendButton).showError();
    }

    public void testHandleError_operatorUnsupportedWithVoiceDisable() throws Exception {
        assertFalse(controller.voiceEnabled);
        controller.handleError(context, new OperatorUnsupportedException(ERROR_MESSAGE,
                TwitterApiErrorConstants.OPERATOR_UNSUPPORTED, createAuthConfig(true, false,
                true)));
        assertFalse(controller.voiceEnabled);
        verify(phoneEditText).setError(ERROR_MESSAGE);
        verify(sendButton).showError();
    }

    public void testRetryScribing() throws Exception {
        controller.errorCount = 1;
        controller.executeRequest(context);
        verify(scribeService).click(DigitsScribeConstants.Element.RETRY);
    }

    private AuthResponse createAuthResponse() {
        final AuthResponse authResponse = new AuthResponse();
        authResponse.requestId = REQUEST_ID;
        authResponse.userId = USER_ID;
        authResponse.authConfig = createAuthConfig(true, true, true);
        return authResponse;
    }

    private AuthResponse createAuthResponseDisabledEmail() {
        final AuthResponse authResponse = new AuthResponse();
        authResponse.requestId = REQUEST_ID;
        authResponse.userId = USER_ID;
        authResponse.authConfig = createAuthConfig(true, true, false);
        return authResponse;
    }

    private AuthConfig createAuthConfig(boolean tosUpdate, boolean isVoiceEnabled,
                                        boolean isEmailEnabled) {
        final AuthConfig authConfig = new AuthConfig();
        authConfig.tosUpdate = tosUpdate;
        authConfig.isVoiceEnabled = isVoiceEnabled;
        authConfig.isEmailEnabled = isEmailEnabled;
        return authConfig;
    }

    public Verification getVerification() {
        return verification;
    }
}
