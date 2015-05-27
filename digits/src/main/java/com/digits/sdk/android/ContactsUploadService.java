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

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import io.fabric.sdk.android.services.concurrency.internal.DefaultRetryPolicy;
import io.fabric.sdk.android.services.concurrency.internal.ExponentialBackoff;
import io.fabric.sdk.android.services.concurrency.internal.RetryThreadPoolExecutor;

public class ContactsUploadService extends IntentService {
    private static final String THREAD_NAME = "UPLOAD_WORKER";
    private static final int TIMEOUT_IN_SECONDS = 300;
    public static final String UPLOAD_COMPLETE = "com.digits.sdk.android.UPLOAD_COMPLETE";
    public static final String UPLOAD_COMPLETE_EXTRA = "com.digits.sdk.android.UPLOAD_COMPLETE_EXTRA";
    public static final String UPLOAD_FAILED = "com.digits.sdk.android.UPLOAD_FAILED";
    private static final int MAX_RETRIES = 1;
    private static final int CORE_THREAD_POOL_SIZE = 2;
    private static final int INITIAL_BACKOFF_MS = 1000;
    private ContactsClient contactsClient;
    private ContactsHelper helper;
    private ContactsPreferenceManager prefManager;
    private RetryThreadPoolExecutor executor;

    public ContactsUploadService() {
        super(THREAD_NAME);

        init(Digits.getInstance().getContactsClient(), new ContactsHelper(this),
                new ContactsPreferenceManager(),
                new RetryThreadPoolExecutor(CORE_THREAD_POOL_SIZE,
                        new DefaultRetryPolicy(MAX_RETRIES),
                        new ExponentialBackoff(INITIAL_BACKOFF_MS)));
    }

    /*
     * Testing only
     */
    ContactsUploadService(ContactsClient contactsClient, ContactsHelper helper,
                          ContactsPreferenceManager prefManager, RetryThreadPoolExecutor executor) {
        super(THREAD_NAME);

        init(contactsClient, helper, prefManager, executor);
    }

    private void init(ContactsClient contactsClient, ContactsHelper helper,
              ContactsPreferenceManager prefManager, RetryThreadPoolExecutor executor) {
        this.contactsClient = contactsClient;
        this.helper = helper;
        this.prefManager = prefManager;
        this.executor = executor;

        setIntentRedelivery(true);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        prefManager.setContactImportPermissionGranted();

        //noinspection TryWithIdenticalCatches
        try {
            final List<String> allCards = getAllCards();
            final int totalCount = allCards.size();
            final int pages = getNumberOfPages(totalCount);
            final AtomicInteger successCount = new AtomicInteger(0);

            for (int i = 0; i < pages; i++) {
                final int startIndex = i * ContactsClient.MAX_PAGE_SIZE;
                final int endIndex = Math.min(totalCount, startIndex +
                        ContactsClient.MAX_PAGE_SIZE);

                final List<String> subList = allCards.subList(startIndex, endIndex);
                final Vcards vCards = new Vcards(subList);
                executor.scheduleWithRetry(new Runnable() {
                    @Override
                    public void run() {
                        contactsClient.uploadContacts(vCards);
                        successCount.addAndGet(vCards.vcards.size());
                    }
                });
            }

            executor.shutdown();
            final boolean success = executor.awaitTermination(TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);

            if (!success) {
                executor.shutdownNow();
                sendFailureBroadcast();
            } else if (successCount.get() == 0) {
                sendFailureBroadcast();
            } else {
                prefManager.setContactsReadTimestamp(System.currentTimeMillis());
                prefManager.setContactsUploaded(successCount.get());
                sendSuccessBroadcast(new ContactsUploadResult(successCount.get(), totalCount));
            }
        } catch (Exception ex) {
            sendFailureBroadcast();
        }
    }

    int getNumberOfPages(int numCards) {
        return (numCards + ContactsClient.MAX_PAGE_SIZE - 1) / ContactsClient.MAX_PAGE_SIZE;
    }

    private List<String> getAllCards() {
        Cursor cursor = null;
        List<String> allCards = Collections.<String>emptyList();

        try {
            cursor = helper.getContactsCursor();
            allCards = helper.createContactList(cursor);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return allCards;
    }

    void sendFailureBroadcast() {
        final Intent localIntent = new Intent(UPLOAD_FAILED);
        sendBroadcast(localIntent);
    }

    void sendSuccessBroadcast(ContactsUploadResult extra) {
        final Intent localIntent = new Intent(UPLOAD_COMPLETE);
        localIntent.putExtra(UPLOAD_COMPLETE_EXTRA, extra);
        sendBroadcast(localIntent);
    }
}
