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

import io.fabric.sdk.android.FabricAndroidTestCase;

import static org.mockito.Mockito.verifyZeroInteractions;

public class DigitsAndroidTestCase extends FabricAndroidTestCase {

    protected static final String TWITTER_URL = "http://twitter.com";
    protected static final String CONSUMER_KEY = "testKey";
    protected static final String CONSUMER_SECRET = "testSecret";
    protected static final String TOKEN = "token";
    protected static final String SECRET = "secret";
    protected static final long USER_ID = 11;

    protected static final String RAW_PHONE = "+123456789";
    protected static final String PHONE = "123456789";
    protected static final String PHONE_NO_COUNTRY_CODE = "23456789";
    protected static final String PHONE_PLUS_SYMBOL_NO_COUNTRY_CODE = "23456789";
    protected static final String US_COUNTRY_CODE = "1";
    protected static final String US_ISO2 = "us";
    protected static final String US_ISO3 = "usa";

    protected void verifyNoInteractions(Object... objects) {
        for (Object object : objects) {
            verifyZeroInteractions(object);
        }
    }
}
