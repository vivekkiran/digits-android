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
import android.widget.EditText;

import io.fabric.sdk.android.services.common.CommonUtils;

import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.SessionManager;

import java.util.Locale;

class PhoneNumberController extends DigitsControllerImpl {
    final CountryListSpinner countryCodeSpinner;
    String phoneNumber;

    PhoneNumberController(ResultReceiver resultReceiver, StateButton stateButton,
            EditText phoneEditText, CountryListSpinner countryCodeSpinner) {
        this(resultReceiver, stateButton, phoneEditText, countryCodeSpinner,
                Digits.getInstance().getDigitsClient(), new PhoneNumberErrorCodes(stateButton
                        .getContext().getResources()),
                Digits.getInstance().getActivityClassManager(), Digits.getSessionManager());

    }

    /**
     * Only for test
     */
    PhoneNumberController(ResultReceiver resultReceiver, StateButton stateButton,
            EditText phoneEditText, CountryListSpinner countryCodeSpinner,
            DigitsClient client, ErrorCodes errors,
            ActivityClassManager activityClassManager,
            SessionManager<DigitsSession> sessionManager) {
        super(resultReceiver, stateButton, phoneEditText, client, errors, activityClassManager,
                sessionManager);
        this.countryCodeSpinner = countryCodeSpinner;
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
            digitsClient.authDevice(context, this, phoneNumber,
                    new DigitsCallback<AuthResponse>(context, this) {
                        @Override
                        public void success(final Result<AuthResponse> result) {
                            sendButton.showFinish();
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

    @Override
    public void handleError(final Context context, DigitsException digitsException) {
        if (digitsException instanceof CouldNotAuthenticateException) {
            digitsClient.registerDevice(phoneNumber, new
                    DigitsCallback<DeviceRegistrationResponse>(context, this) {
                        @Override
                        public void success(Result<DeviceRegistrationResponse> result) {
                            final DeviceRegistrationResponse response = result.data;
                            phoneNumber = response.normalizedPhoneNumber == null ? phoneNumber :
                                    response.normalizedPhoneNumber;
                            sendButton.showFinish();
                            startNextStep(context);
                        }
                    });
        } else {
            super.handleError(context, digitsException);
        }
    }

    @Override
    Uri getTOSUri() {
        return DigitsConstants.DIGITS_TOS;
    }

    void startSignIn(Context context, AuthResponse response) {
        final Intent intent = new Intent(context, activityClassManager.getLoginCodeActivity());
        final Bundle bundle = getBundle();
        bundle.putString(DigitsClient.EXTRA_REQUEST_ID, response.requestId);
        bundle.putLong(DigitsClient.EXTRA_USER_ID, response.userId);
        if (response.authConfig != null) {
            bundle.putBoolean(DigitsClient.EXTRA_TOS_UPDATED, response.authConfig.tosUpdate);
        }
        intent.putExtras(bundle);
        startActivityForResult((Activity) context, intent);
    }

    private void startNextStep(Context context) {
        final Intent intent = new Intent(context, activityClassManager.getConfirmationActivity());
        intent.putExtras(getBundle());
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

}
