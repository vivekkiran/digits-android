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
public class PhoneNumberScribeServiceTest {
    private PhoneNumberScribeService service;
    @Mock
    private DigitsScribeClient client;
    @Captor
    private ArgumentCaptor<EventNamespace> eventNamespaceArgumentCaptor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        service = new PhoneNumberScribeService(client);
    }

    @Test
    public void testImpression() throws Exception {
        service.impression();
        verify(client).scribe(eventNamespaceArgumentCaptor.capture());
        final EventNamespace eventNamespace = eventNamespaceArgumentCaptor.getValue();
        final EventNamespace ns = createImpression();
        assertEquals(ns, eventNamespace);
    }

    @Test
    public void testSuccess() throws Exception {
        service.success();
        verify(client).scribe(eventNamespaceArgumentCaptor.capture());
        final EventNamespace eventNamespace = eventNamespaceArgumentCaptor.getValue();
        final EventNamespace ns = createSuccess();
        assertEquals(ns, eventNamespace);
    }

    @Test
    public void testClick() throws Exception {
        service.click(DigitsScribeConstants.Element.SUBMIT);
        verify(client).scribe(eventNamespaceArgumentCaptor.capture());
        final EventNamespace eventNamespace = eventNamespaceArgumentCaptor.getValue();
        final EventNamespace ns = createClick();
        assertEquals(ns, eventNamespace);
    }

    @Test
    public void testFailure() throws Exception {
        service.failure();
        verify(client).scribe(eventNamespaceArgumentCaptor.capture());
        final EventNamespace eventNamespace = eventNamespaceArgumentCaptor.getValue();
        final EventNamespace ns = createFailure();
        assertEquals(ns, eventNamespace);
    }

    @Test
    public void testError() throws Exception {
        service.error(TestConstants.ANY_EXCEPTION);
        verify(client).scribe(eventNamespaceArgumentCaptor.capture());
        final EventNamespace eventNamespace = eventNamespaceArgumentCaptor.getValue();
        final EventNamespace ns = createException();
        assertEquals(ns, eventNamespace);
    }

    @Test(expected = NullPointerException.class)
    public void testConstructor_withNullScribeClient() throws Exception {
        new PhoneNumberScribeService(null);
    }

    private EventNamespace createException() {
        return DigitsScribeConstants.DIGITS_EVENT_BUILDER
                .setComponent(PhoneNumberScribeService.AUTH_COMPONENT)
                .setElement(DigitsScribeConstants.EMPTY_SCRIBE_ELEMENT)
                .setAction(DigitsScribeConstants.ERROR_ACTION)
                .builder();
    }

    private EventNamespace createImpression() {
        return DigitsScribeConstants.DIGITS_EVENT_BUILDER
                .setComponent(PhoneNumberScribeService.AUTH_COMPONENT)
                .setElement(DigitsScribeConstants.EMPTY_SCRIBE_ELEMENT)
                .setAction(DigitsScribeConstants.IMPRESSION_ACTION)
                .builder();
    }

    private EventNamespace createSuccess() {
        return DigitsScribeConstants.DIGITS_EVENT_BUILDER
                .setComponent(PhoneNumberScribeService.AUTH_COMPONENT)
                .setElement(DigitsScribeConstants.EMPTY_SCRIBE_ELEMENT)
                .setAction(DigitsScribeConstants.SUCCESS_ACTION)
                .builder();
    }

    private EventNamespace createFailure() {
        return DigitsScribeConstants.DIGITS_EVENT_BUILDER
                .setComponent(PhoneNumberScribeService.AUTH_COMPONENT)
                .setElement(DigitsScribeConstants.EMPTY_SCRIBE_ELEMENT)
                .setAction(DigitsScribeConstants.FAILURE_ACTION)
                .builder();
    }

    private EventNamespace createClick() {
        return DigitsScribeConstants.DIGITS_EVENT_BUILDER
                .setComponent(PhoneNumberScribeService.AUTH_COMPONENT)
                .setElement(DigitsScribeConstants.Element.SUBMIT.getElement())
                .setAction(DigitsScribeConstants.CLICK_ACTION)
                .builder();
    }
}
