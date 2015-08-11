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

    PhoneNumberController(ResultReceiver resultReceiver,
                          StateButton stateButton, EditText phoneEditText,
                          CountryListSpinner countryCodeSpinner, TosView tosView) {
        this(resultReceiver, stateButton, phoneEditText, countryCodeSpinner,
                Digits.getInstance().getDigitsClient(), new PhoneNumberErrorCodes(stateButton
                        .getContext().getResources()),
                Digits.getInstance().getActivityClassManager(), Digits.getSessionManager(),
                tosView);
        voiceEnabled = false;
        resendState = false;
    }

    /**
     * Only for test
     */
    PhoneNumberController(ResultReceiver resultReceiver,
                          StateButton stateButton, EditText phoneEditText,
                          CountryListSpinner countryCodeSpinner,
                          DigitsClient client, ErrorCodes errors,
                          ActivityClassManager activityClassManager,
                          SessionManager<DigitsSession> sessionManager, TosView tosView) {
        super(resultReceiver, stateButton, phoneEditText, client, errors, activityClassManager,
                sessionManager);
        this.countryCodeSpinner = countryCodeSpinner;
        this.tosView = tosView;
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

    @NonNull
    private Verification getVerificationType() {
        return resendState && voiceEnabled ? Verification.voicecall : Verification.sms;
    }

    @Override
    public void handleError(final Context context, DigitsException digitsException) {
        if (digitsException instanceof CouldNotAuthenticateException) {
                digitsClient.registerDevice(phoneNumber, getVerificationType(),
                        new
                        DigitsCallback<DeviceRegistrationResponse>(context, this) {
                            @Override
                            public void success(Result<DeviceRegistrationResponse> result) {
                                final DeviceRegistrationResponse response = result.data;
                                final AuthConfig config = response.authConfig;
                                if (config != null) {
                                    voiceEnabled = config.isVoiceEnabled;
                                }
                                phoneNumber = response.normalizedPhoneNumber == null ?
                                        phoneNumber :response.normalizedPhoneNumber;
                                sendButton.showFinish();
                                startNextStep(context, result.data);
                            }
                        });
        } else {
            super.handleError(context, digitsException);
        }
    }

    @Override
    Uri getTosUri() {
        return DigitsConstants.DIGITS_TOS;
    }

    void startSignIn(Context context, AuthResponse response) {
        final Intent intent = new Intent(context, activityClassManager.getLoginCodeActivity());
        final Bundle bundle = getBundle();
        bundle.putString(DigitsClient.EXTRA_REQUEST_ID, response.requestId);
        bundle.putLong(DigitsClient.EXTRA_USER_ID, response.userId);
        bundle.putParcelable(DigitsClient.EXTRA_AUTH_CONFIG, response.authConfig);
        intent.putExtras(bundle);
        startActivityForResult((Activity) context, intent);
    }

    private void startNextStep(Context context, DeviceRegistrationResponse response) {
        final Intent intent = new Intent(context, activityClassManager.getConfirmationActivity());
        final Bundle bundle = getBundle();
        if (response.authConfig != null) {
            bundle.putParcelable(DigitsClient.EXTRA_AUTH_CONFIG, response.authConfig);
        }
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
            sendButton.setStatesText(R.string.dgts__confirmation_send_text,
                    R.string.dgts__confirmation_sending_text,
                    R.string.dgts__confirmation_sent_text);
            sendButton.showStart();
            tosView.setText(R.string.dgts__terms_text);
        }
    }
}
