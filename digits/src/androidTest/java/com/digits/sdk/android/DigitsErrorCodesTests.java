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

import com.twitter.sdk.android.core.TwitterApiErrorConstants;

public class DigitsErrorCodesTests extends DigitsAndroidTestCase {
    //all the API codes are positive integers
    private static final int UNKNOWN_ERROR = -10;
    private DigitsErrorCodes errorCodes;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        errorCodes = new DigitsErrorCodes(getContext().getResources());
    }

    public void testGetMessage_knownError() throws Exception {
        assertEquals(getContext().getString(R.string.dgts__confirmation_error_alternative),
                errorCodes.getMessage(TwitterApiErrorConstants.RATE_LIMIT_EXCEEDED));
    }

    public void testGetMessage_unknownError() throws Exception {
        assertEquals(errorCodes.getDefaultMessage(),
                errorCodes.getMessage(UNKNOWN_ERROR));
    }

    public void testGetDefaultMessage() throws Exception {
        assertEquals(getContext().getString(R.string.dgts__try_again),
                errorCodes.getDefaultMessage());
    }

    public void testGetNetworkError() throws Exception {
        assertEquals(getContext().getString(R.string.dgts__network_error),
                errorCodes.getNetworkError());
    }
}
