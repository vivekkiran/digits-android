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

import io.fabric.sdk.android.services.concurrency.AsyncTask;

class PhoneNumberTask extends AsyncTask<Void, Void, PhoneNumber> {
    private final Listener listener;
    private final PhoneNumberUtils phoneNumberUtils;
    private final String providedPhoneNumber;

    protected PhoneNumberTask(PhoneNumberUtils phoneNumberUtils, Listener listener) {
        if (phoneNumberUtils == null) {
            throw new NullPointerException("phoneNumberUtils can't be null");
        }
        this.listener = listener;
        this.phoneNumberUtils = phoneNumberUtils;
        this.providedPhoneNumber = "";
    }

    protected PhoneNumberTask(PhoneNumberUtils phoneNumberUtils, String providedPhoneNumber,
            Listener listener) {
        if (phoneNumberUtils == null) {
            throw new NullPointerException("phoneNumberUtils can't be null");
        }
        this.listener = listener;
        this.phoneNumberUtils = phoneNumberUtils;
        this.providedPhoneNumber = providedPhoneNumber;
    }

    @Override
    protected PhoneNumber doInBackground(Void... params) {
        return phoneNumberUtils.getPhoneNumber(providedPhoneNumber);
    }


    @Override
    protected void onPostExecute(PhoneNumber phoneNumber) {
        if (listener != null) {
            listener.onLoadComplete(phoneNumber);
        }
    }

    interface Listener {
        void onLoadComplete(PhoneNumber result);
    }
}
