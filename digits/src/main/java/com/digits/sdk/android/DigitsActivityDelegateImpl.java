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
import android.support.annotation.StringRes;
import android.text.Html;
import android.text.Spanned;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

abstract class DigitsActivityDelegateImpl implements DigitsActivityDelegate {

    @Override
    public void onDestroy() {
        // no-op Not currently used by all delegates
    }

    public void setUpSendButton(final Activity activity, final DigitsController controller,
                                StateButton stateButton) {
        stateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.clearError();
                controller.executeRequest(activity);
            }
        });

    }

    public void setUpEditText(final Activity activity, final DigitsController controller,
                              EditText editText) {
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    controller.clearError();
                    controller.executeRequest(activity);
                    return true;
                }
                return false;
            }
        });
        editText.addTextChangedListener(controller.getTextWatcher());
    }

    public void setUpTermsText(final Activity activity, final DigitsController controller,
                               TextView termsText) {
        termsText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.clearError();
                controller.showTOS(activity);
            }
        });
    }

    protected Spanned getFormattedTerms(Activity activity, @StringRes int termsResId) {
        return Html.fromHtml(activity.getString(termsResId, "\"", "<u>", "</u>"));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Activity activity) {
        // no-op Not used by all the delegates
    }
}
