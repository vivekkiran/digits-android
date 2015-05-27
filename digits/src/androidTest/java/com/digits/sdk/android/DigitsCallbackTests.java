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

import android.content.Context;
import android.util.Log;

import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.FabricTestUtils;
import io.fabric.sdk.android.KitStub;
import io.fabric.sdk.android.Logger;

import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class DigitsCallbackTests extends DigitsAndroidTestCase {

    private StubDigitsCallback digitsCallback;
    private DigitsController controller;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        controller = mock(DigitsController.class);
        digitsCallback = new StubDigitsCallback(controller);
        when(controller.getErrors()).thenReturn(mock(ErrorCodes.class));
    }

    public void testFailure() throws Exception {
        try {
            final Logger mockLogger = mock(Logger.class);
            when(mockLogger.isLoggable(Digits.TAG, Log.ERROR)).thenReturn(true);

            final Fabric fabric = new Fabric.Builder(getContext())
                    .kits(new KitStub())
                    .debuggable(false)
                    .logger(mockLogger)
                    .build();
            FabricTestUtils.with(fabric);

            digitsCallback.failure(new TwitterException(""));
            verify(controller).handleError(any(Context.class), any(DigitsException.class));
            verify(mockLogger).e(Digits.TAG, "HTTP Error: , API Error: -1, User Message: null");
        } finally {
            FabricTestUtils.resetFabric();
        }
    }

    public void testSuccess() throws Exception {
        digitsCallback.success(null, null);
        assertTrue(digitsCallback.isCalled);
    }

    private class StubDigitsCallback extends DigitsCallback<String> {
        boolean isCalled = false;

        StubDigitsCallback(DigitsController controller) {
            super(null, controller);
        }

        @Override
        public void success(Result<String> result) {
            isCalled = true;
        }
    }
}
