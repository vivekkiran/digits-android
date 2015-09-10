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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import io.fabric.sdk.android.services.common.CommonUtils;

class FailureActivityDelegateImpl implements FailureActivityDelegate {
    final Activity activity;
    final FailureController controller;
    final DigitsScribeService scribeService;

    public FailureActivityDelegateImpl(Activity activity) {
        this(activity, new FailureControllerImpl(), new FailureScribeService(Digits.getInstance()
                .getScribeClient()));
    }

    public FailureActivityDelegateImpl(Activity activity, FailureController controller,
                                       DigitsScribeService scribeService) {
        this.activity = activity;
        this.controller = controller;
        this.scribeService = scribeService;
    }

    public void init() {
        scribeService.impression();
        final Bundle bundle = activity.getIntent().getExtras();
        if (isBundleValid(bundle)) {
            setContentView();
            setUpViews();
        } else {
            throw new IllegalAccessError("This activity can only be started from Digits");
        }
    }

    protected boolean isBundleValid(Bundle bundle) {
        return BundleManager.assertContains(bundle, DigitsClient.EXTRA_RESULT_RECEIVER);
    }

    protected void setContentView() {
        activity.setContentView(R.layout.dgts__activity_failure);
    }

    protected void setUpViews() {
        final Button dismissButton = (Button) activity.findViewById(R.id.dgts__dismiss_button);
        final TextView tryAnotherNumberButton = (TextView) activity.findViewById(R.id
                .dgts__try_another_phone);

        setUpDismissButton(dismissButton);
        setUpTryAnotherPhoneButton(tryAnotherNumberButton);
    }

    protected void setUpDismissButton(Button button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scribeService.click(DigitsScribeConstants.Element.DISMISS);
                CommonUtils.finishAffinity(activity, DigitsActivity.RESULT_FINISH_DIGITS);
                controller.sendFailure(getBundleResultReceiver(), getBundleException());
            }
        });
    }

    protected void setUpTryAnotherPhoneButton(TextView textView) {
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scribeService.click(DigitsScribeConstants.Element.RETRY);
                controller.tryAnotherNumber(activity, getBundleResultReceiver());
                activity.finish();
            }
        });
    }

    private ResultReceiver getBundleResultReceiver() {
        final Bundle bundle = activity.getIntent().getExtras();
        return bundle.getParcelable(DigitsClient.EXTRA_RESULT_RECEIVER);
    }

    private DigitsException getBundleException() {
        final Bundle bundle = activity.getIntent().getExtras();
        return (DigitsException) bundle.getSerializable(DigitsClient.EXTRA_FALLBACK_REASON);
    }
}
