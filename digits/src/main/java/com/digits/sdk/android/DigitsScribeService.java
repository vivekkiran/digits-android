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

/**
 * Scribing events for Digits
 */
public interface DigitsScribeService {
    /**
     * Scribing event when authentication flow starts
     */
    void authImpression();
    /**
     * Scribing event when authentication flow fails
     */
    void authFailure();
    /**
     * Scribing event when authentication flow is successful
     */
    void authLoggedIn();
    /**
     * Scribing event for {@link PhoneNumberActivity} submit button click
     */
    void phoneNumberActivitySubmitClick();
    /**
     * Scribing event for {@link PhoneNumberActivity} when there is a retry to submit a phone
     */
    void phoneNumberActivityRetryClick();
    /**
     * Scribing events for {@link PhoneNumberActivity}  country code spinner click
     */
    void phoneNumberActivityCountryCodeSpinnerClick();
    /**
     * Scribing events for {@link PhoneNumberActivity} success. Success means that authentication
     * flow moves to the challenge state.
     */
    void phoneNumberActivitySuccess();
}
