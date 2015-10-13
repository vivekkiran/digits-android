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
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.widget.EditText;

import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.SessionManager;

import java.util.Locale;

import io.fabric.sdk.android.services.common.CommonUtils;

class PhoneNumberController extends DigitsControllerImpl implements
        PhoneNumberTask.Listener {
    private final TosView tosView;
    final CountryListSpinner countryCodeSpinner;
    String phoneNumber;
    boolean voiceEnabled;
    boolean resendState;
    boolean emailCollection;

    PhoneNumberController(ResultReceiver resultReceiver,
                          StateButton stateButton, EditText phoneEditText,
                          CountryListSpinner countryCodeSpinner, TosView tosView,
                          DigitsScribeService scribeService, boolean emailCollection) {
        this(resultReceiver, stateButton, phoneEditText, countryCodeSpinner,
                Digits.getInstance().getDigitsClient(), new PhoneNumberErrorCodes(stateButton
                        .getContext().getResources()),
                Digits.getInstance().getActivityClassManager(), Digits.getSessionManager(),
                tosView, scribeService, emailCollection);
    }

    /**
     * Only for test
     */
    PhoneNumberController(ResultReceiver resultReceiver, StateButton stateButton,
                          EditText phoneEditText, CountryListSpinner countryCodeSpinner,
                          DigitsClient client, ErrorCodes errors,
                          ActivityClassManager activityClassManager,
                          SessionManager<DigitsSession> sessionManager, TosView tosView,
                          DigitsScribeService scribeService, boolean emailCollection) {
        super(resultReceiver, stateButton, phoneEditText, client, errors, activityClassManager,
                sessionManager, scribeService);
        this.countryCodeSpinner = countryCodeSpinner;
        this.tosView = tosView;
        voiceEnabled = false;
        resendState = false;
        this.emailCollection = emailCollection;
    }

    public void setPhoneNumber(PhoneNumber phoneNumber) {
        if (PhoneNumber.isValid(phoneNumber)) {
            editText.setText(phoneNumber.getPhoneNumber());
            editText.setSelection(phoneNumber.getPhoneNumber().length());
        }
    }

    public void setCountryCode(PhoneNumber phoneNumber) {
        if (PhoneNumber.isCountryValid(phoneNumber)) {
            countryCodeSpinner.setSelectedForCountry(new Locale("",
                    phoneNumber.getCountryIso()).getDisplayName(), phoneNumber.getCountryCode());
        }
    }

    @Override
    public void executeRequest(final Context context) {
        scribeRequest();
        if (validateInput(editText.getText())) {
            sendButton.showProgress();
            CommonUtils.hideKeyboard(context, editText);
            final int code = (Integer) countryCodeSpinner.getTag();
            final String number = editText.getText().toString();
            phoneNumber = getNumber(code, number);
            digitsClient.authDevice(phoneNumber, getVerificationType(),
                    new DigitsCallback<AuthResponse>(context, this) {
                        @Override
                        public void success(final Result<AuthResponse> result) {
                            sendButton.showFinish();
                            final AuthConfig config = result.data.authConfig;
                            if (config != null) {
                                voiceEnabled = config.isVoiceEnabled;
                                emailCollection = config.isEmailEnabled && emailCollection;
                            }
                            editText.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    final AuthResponse response = result.data;
                                    phoneNumber = response.normalizedPhoneNumber == null ?
                                            phoneNumber : response.normalizedPhoneNumber;
                                    startSignIn(context, result.data);
                                }
                            }, POST_DELAY_MS);
                        }
                    }
            );

        }
    }

    private void scribeRequest() {
        if (isRetry()) {
            scribeService.click(DigitsScribeConstants.Element.RETRY);
        } else {
            scribeService.click(DigitsScribeConstants.Element.SUBMIT);
        }
    }

    private boolean isRetry() {
        return errorCount > 0;
    }

    @NonNull
    private Verification getVerificationType() {
        return resendState && voiceEnabled ? Verification.voicecall : Verification.sms;
    }

    @Override
    public void handleError(final Context context, DigitsException digitsException) {
        if (digitsException instanceof CouldNotAuthenticateException) {
            digitsClient.registerDevice(phoneNumber, getVerificationType(),
                    new DigitsCallback<DeviceRegistrationResponse>(context, this) {
                        @Override
                        public void success(Result<DeviceRegistrationResponse> result) {
                            final DeviceRegistrationResponse response = result.data;
                            final AuthConfig config = response.authConfig;
                            if (config != null) {
                                voiceEnabled = config.isVoiceEnabled;
                                emailCollection = config.isEmailEnabled && emailCollection;
                            }
                            phoneNumber = response.normalizedPhoneNumber == null ?
                                    phoneNumber : response.normalizedPhoneNumber;
                            sendButton.showFinish();
                            startNextStep(context, result.data);
                        }
                    });
        } else if (digitsException instanceof OperatorUnsupportedException) {
            voiceEnabled = digitsException.getConfig().isVoiceEnabled;
            resend();
            super.handleError(context, digitsException);
        } else {
            super.handleError(context, digitsException);
        }
    }

    @Override
    Uri getTosUri() {
        return DigitsConstants.DIGITS_TOS;
    }

    void startSignIn(Context context, AuthResponse response) {
        scribeService.success();
        final Intent intent = new Intent(context, activityClassManager.getLoginCodeActivity());
        final Bundle bundle = getBundle();
        bundle.putString(DigitsClient.EXTRA_REQUEST_ID, response.requestId);
        bundle.putLong(DigitsClient.EXTRA_USER_ID, response.userId);
        bundle.putParcelable(DigitsClient.EXTRA_AUTH_CONFIG, response.authConfig);
        bundle.putBoolean(DigitsClient.EXTRA_EMAIL, emailCollection);
        intent.putExtras(bundle);
        startActivityForResult((Activity) context, intent);
    }

    private void startNextStep(Context context, DeviceRegistrationResponse response) {
        scribeService.success();
        final Intent intent = new Intent(context, activityClassManager.getConfirmationActivity());
        final Bundle bundle = getBundle();
        bundle.putParcelable(DigitsClient.EXTRA_AUTH_CONFIG, response.authConfig);
        bundle.putBoolean(DigitsClient.EXTRA_EMAIL, emailCollection);
        intent.putExtras(bundle);
        startActivityForResult((Activity) context, intent);
    }


    private Bundle getBundle() {
        final Bundle bundle = new Bundle();
        bundle.putString(DigitsClient.EXTRA_PHONE, phoneNumber);
        bundle.putParcelable(DigitsClient.EXTRA_RESULT_RECEIVER, resultReceiver);
        return bundle;
    }

    private String getNumber(long countryCode, String numberTextView) {
        return "+" + String.valueOf(countryCode) + numberTextView;
    }

    public void onLoadComplete(PhoneNumber phoneNumber) {
        setPhoneNumber(phoneNumber);
        setCountryCode(phoneNumber);
    }


    public void resend() {
        resendState = true;
        if (voiceEnabled) {
            sendButton.setStatesText(R.string.dgts__call_me, R.string.dgts__calling,
                    R.string.dgts__calling);
            tosView.setText(R.string.dgts__terms_text_call_me);
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        super.onTextChanged(s, start, before, count);
        if (Verification.voicecall.equals(getVerificationType())) {
            resendState = false;
            sendButton.setStatesText(R.string.dgts__continue,
                    R.string.dgts__sending,
                    R.string.dgts__done);
            sendButton.showStart();
            tosView.setText(R.string.dgts__terms_text);
        }
    }
}
