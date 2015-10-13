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
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import io.fabric.sdk.android.services.common.CommonUtils;


public class EmailRequestActivityDelegate extends DigitsActivityDelegateImpl {
    EditText editText;
    StateButton stateButton;
    TextView termsText;
    TextView resendText;
    DigitsController controller;
    Activity activity;
    DigitsScribeService scribeService;
    TextView titleText;

    EmailRequestActivityDelegate(DigitsScribeService scribeService) {
        this.scribeService = scribeService;
    }

    @Override
    public int getLayoutId() {
        return R.layout.dgts__activity_confirmation;
    }

    @Override
    public boolean isValid(Bundle bundle) {
        return BundleManager.assertContains(bundle, DigitsClient.EXTRA_RESULT_RECEIVER,
                DigitsClient.EXTRA_PHONE);
    }

    @Override
    public void init(Activity activity, Bundle bundle) {
        this.activity = activity;
        titleText = (TextView) activity.findViewById(R.id.dgts__titleText);
        editText = (EditText) activity.findViewById(R.id.dgts__confirmationEditText);
        stateButton = (StateButton) activity.findViewById(R.id.dgts__createAccount);
        termsText = (TextView) activity.findViewById(R.id.dgts__termsTextCreateAccount);
        resendText = (TextView) activity.findViewById(R.id.dgts__resendConfirmation);

        controller = initController(bundle);

        editText.setHint(R.string.dgts__email_request_edit_hint);
        titleText.setText(R.string.dgts__email_request_title);

        setUpEditText(activity, controller, editText);
        setUpSendButton(activity, controller, stateButton);
        setUpTermsText(activity, controller, termsText);
        setUpResendText(resendText);

        CommonUtils.openKeyboard(activity, editText);
    }

    @Override
    public void setUpEditText(final Activity activity, final DigitsController controller,
                              EditText editText) {
        editText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        super.setUpEditText(activity, controller, editText);
    }

    @Override
    public void setUpTermsText(Activity activity, DigitsController controller, TextView termsText) {
        termsText.setText(getFormattedTerms(activity, R.string.dgts__terms_email_request));
        super.setUpTermsText(activity, controller, termsText);
    }

    private void setUpResendText(TextView resendText) {
        resendText.setVisibility(View.GONE);
    }

    private DigitsController initController(Bundle bundle) {
        return new EmailRequestController(stateButton, editText,
                bundle.<ResultReceiver>getParcelable(DigitsClient.EXTRA_RESULT_RECEIVER),
                bundle.getString(DigitsClient.EXTRA_PHONE), scribeService);
    }

    @Override
    public void onResume() {
        scribeService.impression();
        controller.onResume();
    }
}
