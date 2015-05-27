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
import android.content.Intent;
import android.database.Cursor;

import io.fabric.sdk.android.services.concurrency.internal.RetryThreadPoolExecutor;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class ContactsUploadServiceTests extends DigitsAndroidTestCase {
    private Cursor cursor;
    private ContactsHelper helper;
    private RetryThreadPoolExecutor executor;
    private ContactsClient contactsClient;
    private ContactsPreferenceManager perfManager;
    private ArrayList<String> cradList;
    private StubContractsService service;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        executor = mock(RetryThreadPoolExecutor.class);
        perfManager = mock(MockContactsPreferenceManager.class);
        contactsClient = mock(StubContactsClient.class);
        cursor = ContactsHelperTests.createCursor();
        cradList = ContactsHelperTests.createCardList();

        helper = mock(StubContactsHelper.class);
        when(helper.getContactsCursor()).thenReturn(cursor);
        when(helper.createContactList(cursor)).thenReturn(cradList);

        service = new StubContractsService(contactsClient, helper, perfManager, executor);
    }

    public void testOnHandleIntent() throws Exception {
        when(executor.awaitTermination(anyLong(), any(TimeUnit.class))).thenReturn(true);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                ((Runnable) invocationOnMock.getArguments()[0]).run();
                return null;
            }
        }).when(executor).scheduleWithRetry(any(Runnable.class));

        service.onHandleIntent(null);

        verify(helper).getContactsCursor();
        verify(helper).createContactList(cursor);
        verify(executor).scheduleWithRetry(any(Runnable.class));
        verify(executor).shutdown();
        verify(executor).awaitTermination(anyLong(), any(TimeUnit.class));

        assertTrue(service.sendBroadcastCalled);
        assertEquals(ContactsUploadService.UPLOAD_COMPLETE, service.broadcastIntent.getAction());

        verify(perfManager).setContactImportPermissionGranted();
        verify(perfManager).setContactsUploaded(cradList.size());
        verify(perfManager).setContactsReadTimestamp(anyLong());

        final ContactsUploadResult result = service.broadcastIntent
                .getParcelableExtra(ContactsUploadService.UPLOAD_COMPLETE_EXTRA);
        assertEquals(cradList.size(), result.successCount);
        assertEquals(cradList.size(), result.totalCount);
    }

    public void testOnHandleIntent_uploadTimeout() throws Exception {
        when(executor.awaitTermination(anyLong(), any(TimeUnit.class))).thenReturn(false);

        service.onHandleIntent(null);

        verify(helper).getContactsCursor();
        verify(helper).createContactList(cursor);
        verify(executor).scheduleWithRetry(any(Runnable.class));
        verify(executor).shutdown();
        verify(executor).awaitTermination(anyLong(), any(TimeUnit.class));
        verify(executor).shutdownNow();

        assertTrue(service.sendBroadcastCalled);
        assertEquals(ContactsUploadService.UPLOAD_FAILED, service.broadcastIntent.getAction());

        verify(perfManager).setContactImportPermissionGranted();
        verifyNoMoreInteractions(perfManager);
    }

    public void testGetNumberOfPages() {
        assertEquals(1, service.getNumberOfPages(100));
        assertEquals(1, service.getNumberOfPages(50));
        assertEquals(2, service.getNumberOfPages(101));
        assertEquals(2, service.getNumberOfPages(199));
    }

    public void testSendFailureBroadcast() {
        service.sendFailureBroadcast();

        assertTrue(service.sendBroadcastCalled);
        assertEquals(ContactsUploadService.UPLOAD_FAILED, service.broadcastIntent.getAction());
    }

    public void testSendSuccessBroadcast() {
        service.sendSuccessBroadcast(new ContactsUploadResult(1, 1));

        assertTrue(service.sendBroadcastCalled);
        assertEquals(ContactsUploadService.UPLOAD_COMPLETE, service.broadcastIntent.getAction());
        final ContactsUploadResult result = service.broadcastIntent
                .getParcelableExtra(ContactsUploadService.UPLOAD_COMPLETE_EXTRA);
        assertEquals(1, result.successCount);
        assertEquals(1, result.totalCount);
    }

    public class StubContactsHelper extends ContactsHelper {

        public StubContactsHelper(Context context) {
            super(context);
        }
    }

    public class StubContractsService extends ContactsUploadService {
        boolean sendBroadcastCalled = false;
        Intent broadcastIntent;

        StubContractsService(ContactsClient contactsClient, ContactsHelper helper,
                ContactsPreferenceManager perfManager, RetryThreadPoolExecutor executor) {
            super(contactsClient, helper, perfManager, executor);
        }

        @Override
        public void sendBroadcast(Intent intent) {
            sendBroadcastCalled = true;
            broadcastIntent = intent;
        }
    }

    public class StubContactsClient extends ContactsClient {
        public UploadResponse uploadContacts(Vcards vcards) {
            return null;
        }
    }
}
