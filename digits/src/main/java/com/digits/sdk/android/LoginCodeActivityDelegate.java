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
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import io.fabric.sdk.android.services.common.CommonUtils;

class LoginCodeActivityDelegate extends DigitsActivityDelegateImpl {
    private final DigitsScribeService scribeService;
    EditText editText;
    StateButton stateButton;
    TextView termsText;
    DigitsController controller;
    SmsBroadcastReceiver receiver;
    Activity activity;
    AuthConfig config;

    LoginCodeActivityDelegate(DigitsScribeService scribeService) {
        this.scribeService = scribeService;
    }

    @Override
    public void init(Activity activity, Bundle bundle) {
        this.activity = activity;
        editText = (EditText) activity.findViewById(R.id.dgts__confirmationEditText);
        stateButton = (StateButton) activity.findViewById(R.id.dgts__createAccount);
        termsText = (TextView) activity.findViewById(R.id.dgts__termsTextCreateAccount);
        final TextView resendText = (TextView) activity.findViewById(R.id.dgts__resendConfirmation);
        config = bundle.getParcelable(DigitsClient.EXTRA_AUTH_CONFIG);

        controller = initController(bundle);

        setUpEditText(activity, controller, editText);
        setUpSendButton(activity, controller, stateButton);
        setUpTermsText(activity, controller, termsText);
        setUpResendText(activity, resendText);
        setUpSmsIntercept(activity, editText);

        CommonUtils.openKeyboard(activity, editText);
    }

    DigitsController initController(Bundle bundle) {
        return new LoginCodeController(bundle
                .<ResultReceiver>getParcelable(DigitsClient.EXTRA_RESULT_RECEIVER),
                stateButton, editText, bundle.getString(DigitsClient.EXTRA_REQUEST_ID),
                bundle.getLong(DigitsClient.EXTRA_USER_ID), bundle.getString(DigitsClient
                .EXTRA_PHONE), scribeService, bundle.getBoolean(DigitsClient.EXTRA_EMAIL));
    }

    @Override
    public void setUpTermsText(Activity activity, DigitsController controller, TextView termsText) {
        if (config != null && config.tosUpdate) {
            termsText.setText(getFormattedTerms(activity, R.string.dgts__terms_text_sign_in));
            super.setUpTermsText(activity, controller, termsText);
        } else {
            termsText.setVisibility(View.GONE);
        }
    }

    protected void setUpResendText(final Activity activity, TextView resendText) {
        resendText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scribeService.click(DigitsScribeConstants.Element.RESEND);
                activity.setResult(DigitsActivity.RESULT_RESEND_CONFIRMATION);
                activity.finish();
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.dgts__activity_confirmation;
    }

    @Override
    public boolean isValid(Bundle bundle) {
        return BundleManager.assertContains(bundle, DigitsClient.EXTRA_RESULT_RECEIVER,
                DigitsClient.EXTRA_PHONE, DigitsClient.EXTRA_REQUEST_ID,
                DigitsClient.EXTRA_USER_ID);
    }

    @Override
    public void onResume() {
        scribeService.impression();
        controller.onResume();
    }

    @Override
    public void onDestroy() {
        if (receiver != null) {
            activity.unregisterReceiver(receiver);
        }
    }

    protected void setUpSmsIntercept(Activity activity, EditText editText) {
        if (CommonUtils.checkPermission(activity, "android.permission.RECEIVE_SMS")) {
            final IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
            receiver = new SmsBroadcastReceiver(editText);
            activity.registerReceiver(receiver, filter);
        }
    }
}
