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
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import io.fabric.sdk.android.services.common.CommonUtils;

class PhoneNumberActivityDelegate extends DigitsActivityDelegateImpl implements
        PhoneNumberTask.Listener, TosView {
    private final DigitsScribeService scribeService;
    private Activity activity;

    CountryListSpinner countryCodeSpinner;
    StateButton sendButton;
    EditText phoneEditText;
    TextView termsTextView;
    PhoneNumberController controller;

    public PhoneNumberActivityDelegate(DigitsScribeService scribeService) {
        this.scribeService = scribeService;
    }

    @Override
    public int getLayoutId() {
        return R.layout.dgts__activity_phone_number;
    }

    @Override
    public boolean isValid(Bundle bundle) {
        return BundleManager.assertContains(bundle, DigitsClient.EXTRA_RESULT_RECEIVER);
    }

    @Override
    public void init(Activity activity, Bundle bundle) {
        this.activity = activity;
        countryCodeSpinner = (CountryListSpinner) activity.findViewById(R.id.dgts__countryCode);
        sendButton = (StateButton) activity.findViewById(R.id.dgts__sendCodeButton);
        phoneEditText = (EditText) activity.findViewById(R.id.dgts__phoneNumberEditText);
        termsTextView = (TextView) activity.findViewById(R.id.dgts__termsText);
        controller = initController(bundle);

        setUpEditText(activity, controller, phoneEditText);

        setUpSendButton(activity, controller, sendButton);

        setUpTermsText(activity, controller, termsTextView);

        setUpCountrySpinner(countryCodeSpinner);

        executePhoneNumberTask(new PhoneNumberUtils(SimManager.createSimManager(activity)),
                bundle);

        CommonUtils.openKeyboard(activity, phoneEditText);
    }

    private void executePhoneNumberTask(PhoneNumberUtils phoneNumberUtils, Bundle bundle) {
        final String phoneNumber = bundle.getString(DigitsClient.EXTRA_PHONE);

        if (TextUtils.isEmpty(phoneNumber)) {
            new PhoneNumberTask(phoneNumberUtils, this).executeOnExecutor(Digits.getInstance()
                    .getExecutorService());
        } else {
            new PhoneNumberTask(phoneNumberUtils, phoneNumber, this).executeOnExecutor(Digits
                    .getInstance().getExecutorService());
        }
    }

    PhoneNumberController initController(Bundle bundle) {
        return new PhoneNumberController(bundle
                .<ResultReceiver>getParcelable(DigitsClient.EXTRA_RESULT_RECEIVER), sendButton,
                phoneEditText, countryCodeSpinner, this, scribeService, bundle.getBoolean
                (DigitsClient.EXTRA_EMAIL));
    }

    @Override
    public void setUpTermsText(Activity activity, DigitsController controller, TextView termsText) {
        termsText.setText(getFormattedTerms(activity, R.string.dgts__terms_text));
        super.setUpTermsText(activity, controller, termsText);
    }

    protected void setUpCountrySpinner(CountryListSpinner countryCodeSpinner) {
        countryCodeSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scribeService.click(DigitsScribeConstants.Element.COUNTRY_CODE);
                controller.clearError();
            }
        });
    }

    @Override
    public void onResume() {
        scribeService.impression();
        controller.onResume();
    }

    public void onLoadComplete(PhoneNumber phoneNumber) {
        controller.setPhoneNumber(phoneNumber);
        controller.setCountryCode(phoneNumber);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Activity activity) {
        if (resultCode == DigitsActivity.RESULT_RESEND_CONFIRMATION &&
                requestCode == DigitsActivity.REQUEST_CODE) {
            controller.resend();
        }
    }

    @Override
    public void setText(int resourceId) {
        termsTextView.setText(getFormattedTerms(activity, resourceId));
    }
}
