package com.digits.sdk.android;

import com.twitter.sdk.android.core.internal.scribe.DefaultScribeClient;
import com.twitter.sdk.android.core.internal.scribe.EventNamespace;

import junit.framework.TestCase;

import org.mockito.ArgumentCaptor;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class DigitsScribeServiceImplTest extends TestCase {
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

    public void testConstructor_withNullScribeClient() throws Exception {
        try {
            new DigitsScribeServiceImp(null);
            fail();
        } catch (NullPointerException ex) {
        }
    }
}
