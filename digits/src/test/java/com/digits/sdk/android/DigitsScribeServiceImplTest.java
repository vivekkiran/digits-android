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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 21)
public class DigitsScribeServiceImplTest  {
    @Test
    public void testDailyPing() throws Exception {
        final DefaultScribeClient client = mock(DefaultScribeClient.class);
        final DigitsScribeService service = new DigitsScribeServiceImp(client);
        service.dailyPing();
        final ArgumentCaptor<EventNamespace> eventNamespaceArgumentCaptor = ArgumentCaptor.forClass
                (EventNamespace.class);
        verify(client).scribeSyndicatedSdkImpressionEvents(eventNamespaceArgumentCaptor.capture());
        final EventNamespace eventNamespace = eventNamespaceArgumentCaptor.getValue();
        final EventNamespace ns = getDailyPingEvent();
        assertEquals(ns, eventNamespace);
    }

    private EventNamespace getDailyPingEvent() {
        return new EventNamespace.Builder()
                .setClient(DigitsScribeServiceImp.SCRIBE_CLIENT)
                .setPage(DigitsScribeServiceImp.SCRIBE_PAGE)
                .setSection(DigitsScribeServiceImp.EMPTY_SCRIBE_SECTION)
                .setComponent(DigitsScribeServiceImp.EMPTY_SCRIBE_COMPONENT)
                .setElement(DigitsScribeServiceImp.EMPTY_SCRIBE_ELEMENT)
                .setAction(DigitsScribeServiceImp.IMPRESSION_ACTION)
                .builder();
    }

    @Test
    public void testConstructor_withNullScribeClient() throws Exception {
        try {
            new DigitsScribeServiceImp(null);
            fail();
        } catch (NullPointerException ex) {
        }
    }
}
