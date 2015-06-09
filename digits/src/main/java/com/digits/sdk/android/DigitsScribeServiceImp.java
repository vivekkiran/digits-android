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

    static final String EMPTY_SCRIBE_SECTION = "";
    static final String EMPTY_SCRIBE_COMPONENT = "";
    static final String EMPTY_SCRIBE_ELEMENT = "";
    static final String SCRIBE_CLIENT = "android";
    static final String SCRIBE_PAGE = "digits";
    static final String IMPRESSION_ACTION = "impression";

    private final DefaultScribeClient scribeClient;

    public DigitsScribeServiceImp(DefaultScribeClient scribeClient) {
        if (scribeClient == null) {
            throw new NullPointerException("scribeClient must not be null");
        }
        this.scribeClient = scribeClient;
    }

    @Override
    public void dailyPing() {
        final EventNamespace ns = new EventNamespace.Builder()
                .setClient(SCRIBE_CLIENT)
                .setPage(SCRIBE_PAGE)
                .setSection(EMPTY_SCRIBE_SECTION)
                .setComponent(EMPTY_SCRIBE_COMPONENT)
                .setElement(EMPTY_SCRIBE_ELEMENT)
                .setAction(IMPRESSION_ACTION)
                .builder();

        scribe(ns);
    }

    private void scribe(EventNamespace ns) {
        scribeClient.scribeSyndicatedSdkImpressionEvents(ns);
    }
}
