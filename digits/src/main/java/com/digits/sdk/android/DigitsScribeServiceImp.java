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

import com.twitter.sdk.android.core.internal.scribe.DefaultScribeClient;
import com.twitter.sdk.android.core.internal.scribe.EventNamespace;

class DigitsScribeServiceImp implements DigitsScribeService {
    /**
     * Event specs {@see http://go/daas }
     */
    static final String SCRIBE_CLIENT = "tfw";
    static final String SCRIBE_PAGE = "android";
    static final String SCRIBE_SECTION = "digits";

    static final String EMPTY_SCRIBE_COMPONENT = "";
    static final String AUTH_COMPONENT = "auth";

    static final String EMPTY_SCRIBE_ELEMENT = "";
    static final String SUBMIT_ELEMENT = "submit";
    static final String RETRY_ELEMENT = "retry";
    static final String COUNTRY_CODE_ELEMENT = "country_code";

    static final String IMPRESSION_ACTION = "impression";
    static final String LOGGED_IN_ACTION = "logged_in";
    static final String FAILURE_ACTION = "failure";
    static final String SUCCESS_ACTION = "success";
    static final String CLICK_ACTION = "click";

    static final EventNamespace.Builder DIGITS_EVENT_BUILDER = new EventNamespace.Builder()
            .setClient(SCRIBE_CLIENT)
            .setPage(SCRIBE_PAGE)
            .setSection(SCRIBE_SECTION);

    private final DefaultScribeClient scribeClient;

    public DigitsScribeServiceImp(DefaultScribeClient scribeClient) {
        if (scribeClient == null) {
            throw new NullPointerException("scribeClient must not be null");
        }
        this.scribeClient = scribeClient;
    }

    @Override
    public void phoneNumberActivitySubmitClick() {
        final EventNamespace ns = DIGITS_EVENT_BUILDER
                .setComponent(AUTH_COMPONENT)
                .setElement(SUBMIT_ELEMENT)
                .setAction(CLICK_ACTION)
                .builder();

        scribe(ns);
    }

    @Override
    public void phoneNumberActivityRetryClick() {
        final EventNamespace ns = DIGITS_EVENT_BUILDER
                .setComponent(AUTH_COMPONENT)
                .setElement(RETRY_ELEMENT)
                .setAction(CLICK_ACTION)
                .builder();

        scribe(ns);
    }

    @Override
    public void phoneNumberActivityCountryCodeSpinnerClick() {
        final EventNamespace ns = DIGITS_EVENT_BUILDER
                .setComponent(AUTH_COMPONENT)
                .setElement(COUNTRY_CODE_ELEMENT)
                .setAction(CLICK_ACTION)
                .builder();

        scribe(ns);
    }

    @Override
    public void phoneNumberActivitySuccess() {
        final EventNamespace ns = DIGITS_EVENT_BUILDER
                .setComponent(AUTH_COMPONENT)
                .setElement(EMPTY_SCRIBE_ELEMENT)
                .setAction(SUCCESS_ACTION)
                .builder();

        scribe(ns);
    }

    @Override
    public void authImpression() {
        final EventNamespace ns = DIGITS_EVENT_BUILDER
                .setComponent(EMPTY_SCRIBE_COMPONENT)
                .setElement(EMPTY_SCRIBE_ELEMENT)
                .setAction(IMPRESSION_ACTION)
                .builder();

        scribe(ns);
    }

    @Override
    public void authFailure() {
        final EventNamespace ns = DIGITS_EVENT_BUILDER
                .setComponent(EMPTY_SCRIBE_COMPONENT)
                .setElement(EMPTY_SCRIBE_ELEMENT)
                .setAction(FAILURE_ACTION)
                .builder();

        scribe(ns);
    }

    @Override
    public void authLoggedIn() {
        final EventNamespace ns = DIGITS_EVENT_BUILDER
                .setComponent(EMPTY_SCRIBE_COMPONENT)
                .setElement(EMPTY_SCRIBE_ELEMENT)
                .setAction(LOGGED_IN_ACTION)
                .builder();

        scribe(ns);
    }

    private void scribe(EventNamespace ns) {
        scribeClient.scribeSyndicatedSdkImpressionEvents(ns);
    }
}
