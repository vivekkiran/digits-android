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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class DigitsScribeServiceImplTest {
    private DigitsScribeServiceImp service;
    @Mock
    private DefaultScribeClient client;
    @Captor
    private ArgumentCaptor<EventNamespace> eventNamespaceArgumentCaptor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        service = new DigitsScribeServiceImp(client);
    }

    @Test
    public void testAuthImpression() throws Exception {
        service.authImpression();
        verify(client).scribeSyndicatedSdkImpressionEvents(eventNamespaceArgumentCaptor.capture());
        final EventNamespace eventNamespace = eventNamespaceArgumentCaptor.getValue();
        final EventNamespace ns = createAuthImpression();
        assertEquals(ns, eventNamespace);
    }

    @Test
    public void testAuthSuccess() throws Exception {
        service.authLoggedIn();
        verify(client).scribeSyndicatedSdkImpressionEvents(eventNamespaceArgumentCaptor.capture());
        final EventNamespace eventNamespace = eventNamespaceArgumentCaptor.getValue();
        final EventNamespace ns = createAuthSuccess();
        assertEquals(ns, eventNamespace);
    }

    @Test
    public void testAuthFailure() throws Exception {
        service.authFailure();
        verify(client).scribeSyndicatedSdkImpressionEvents(eventNamespaceArgumentCaptor.capture());
        final EventNamespace eventNamespace = eventNamespaceArgumentCaptor.getValue();
        final EventNamespace ns = createAuthFailure();
        assertEquals(ns, eventNamespace);
    }

    private EventNamespace createAuthImpression() {
        return DigitsScribeServiceImp.DIGITS_EVENT_BUILDER
                .setComponent(DigitsScribeServiceImp.EMPTY_SCRIBE_COMPONENT)
                .setElement(DigitsScribeServiceImp.EMPTY_SCRIBE_ELEMENT)
                .setAction(DigitsScribeServiceImp.IMPRESSION_ACTION)
                .builder();
    }

    private EventNamespace createAuthSuccess() {
        return DigitsScribeServiceImp.DIGITS_EVENT_BUILDER
                .setComponent(DigitsScribeServiceImp.EMPTY_SCRIBE_COMPONENT)
                .setElement(DigitsScribeServiceImp.EMPTY_SCRIBE_ELEMENT)
                .setAction(DigitsScribeServiceImp.LOGGED_IN_ACTION)
                .builder();
    }

    private EventNamespace createAuthFailure() {
        return DigitsScribeServiceImp.DIGITS_EVENT_BUILDER
                .setComponent(DigitsScribeServiceImp.EMPTY_SCRIBE_COMPONENT)
                .setElement(DigitsScribeServiceImp.EMPTY_SCRIBE_ELEMENT)
                .setAction(DigitsScribeServiceImp.FAILURE_ACTION)
                .builder();
    }

    @Test(expected = NullPointerException.class)
    public void testConstructor_withNullScribeClient() throws Exception {
        new DigitsScribeServiceImp(null);
    }
}
