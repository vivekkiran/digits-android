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

import android.content.Intent;
import android.database.Cursor;

import io.fabric.sdk.android.services.concurrency.internal.RetryThreadPoolExecutor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class ContactsUploadServiceTests {
    private Cursor cursor;
    private ContactsHelper helper;
    private RetryThreadPoolExecutor executor;
    private ContactsClient contactsClient;
    private ContactsPreferenceManager perfManager;
    private ArrayList<String> cradList;
    private ContactsUploadService service;
    private ArgumentCaptor<Intent> intentCaptor;

    @Before
    public void setUp() throws Exception {
        executor = mock(RetryThreadPoolExecutor.class);
        perfManager = mock(MockContactsPreferenceManager.class);
        contactsClient = mock(ContactsClient.class);
        cursor = ContactsHelperTests.createCursor();
        cradList = ContactsHelperTests.createCardList();
        intentCaptor = ArgumentCaptor.forClass(Intent.class);

        helper = mock(ContactsHelper.class);
        when(helper.getContactsCursor()).thenReturn(cursor);
        when(helper.createContactList(cursor)).thenReturn(cradList);

        service = spy(new ContactsUploadService(contactsClient, helper, perfManager, executor));
    }

    @Test
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

        verify(service).sendBroadcast(intentCaptor.capture());
        assertEquals(ContactsUploadService.UPLOAD_COMPLETE, intentCaptor.getValue().getAction());

        verify(perfManager).setContactImportPermissionGranted();
        verify(perfManager).setContactsUploaded(cradList.size());
        verify(perfManager).setContactsReadTimestamp(anyLong());

        final ContactsUploadResult result = intentCaptor.getValue()
                .getParcelableExtra(ContactsUploadService.UPLOAD_COMPLETE_EXTRA);
        assertEquals(cradList.size(), result.successCount);
        assertEquals(cradList.size(), result.totalCount);
    }

    @Test
    public void testOnHandleIntent_uploadTimeout() throws Exception {
        when(executor.awaitTermination(anyLong(), any(TimeUnit.class))).thenReturn(false);

        service.onHandleIntent(null);

        verify(helper).getContactsCursor();
        verify(helper).createContactList(cursor);
        verify(executor).scheduleWithRetry(any(Runnable.class));
        verify(executor).shutdown();
        verify(executor).awaitTermination(anyLong(), any(TimeUnit.class));
        verify(executor).shutdownNow();

        verify(service).sendBroadcast(intentCaptor.capture());
        assertEquals(ContactsUploadService.UPLOAD_FAILED, intentCaptor.getValue().getAction());

        verify(perfManager).setContactImportPermissionGranted();
        verifyNoMoreInteractions(perfManager);
    }

    @Test
    public void testGetNumberOfPages() {
        assertEquals(1, service.getNumberOfPages(100));
        assertEquals(1, service.getNumberOfPages(50));
        assertEquals(2, service.getNumberOfPages(101));
        assertEquals(2, service.getNumberOfPages(199));
    }

    @Test
    public void testSendFailureBroadcast() {
        service.sendFailureBroadcast();

        verify(service).sendBroadcast(intentCaptor.capture());
        assertEquals(ContactsUploadService.UPLOAD_FAILED, intentCaptor.getValue().getAction());
    }

    @Test
    public void testSendSuccessBroadcast() {
        service.sendSuccessBroadcast(new ContactsUploadResult(1, 1));

        verify(service).sendBroadcast(intentCaptor.capture());
        assertEquals(ContactsUploadService.UPLOAD_COMPLETE, intentCaptor.getValue().getAction());
        final ContactsUploadResult result = intentCaptor.getValue()
                .getParcelableExtra(ContactsUploadService.UPLOAD_COMPLETE_EXTRA);
        assertEquals(1, result.successCount);
        assertEquals(1, result.totalCount);
    }
}
