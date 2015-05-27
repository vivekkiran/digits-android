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
