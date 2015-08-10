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

import android.content.ComponentName;
import android.content.Intent;
import android.test.mock.MockContext;

import com.twitter.sdk.android.core.TwitterCore;

import com.digits.sdk.android.ContactsClient.ContactsService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class ContactsClientTests {
    private TwitterCore twitterCore;
    private MockContext context;
    private ContactsClient contactsClient;
    private ContactsService contactsService;
    private ComponentName activityComponent;
    private ComponentName serviceComponent;
    private ContactsCallback callback;
    private ContactsPreferenceManager prefManager;
    private ActivityClassManagerFactory activityClassManagerFactory;
    ArgumentCaptor<Intent> intentArgumentCaptor;

    @Before
    public void setUp() throws Exception {

        twitterCore = mock(TwitterCore.class);
        activityClassManagerFactory = new ActivityClassManagerFactory();
        context = mock(MockContext.class);
        callback = mock(ContactsCallback.class);
        contactsService = mock(ContactsService.class);
        prefManager = mock(ContactsPreferenceManager.class);
        final Digits digits = mock(Digits.class);
        contactsClient = new ContactsClient(twitterCore, prefManager, activityClassManagerFactory,
                contactsService);
        intentArgumentCaptor = ArgumentCaptor.forClass(Intent.class);

        when(twitterCore.getContext()).thenReturn(context);
        when(context.getPackageName()).thenReturn(getClass().getPackage().toString());
        when(digits.getActivityClassManager()).thenReturn(new ActivityClassManagerImp());

        activityComponent = new ComponentName(context, ContactsActivity.class.getName());
        serviceComponent = new ComponentName(context, ContactsUploadService.class.getName());
    }

    @Test
    public void testConstructor_nullTwitter() throws Exception {
        try {
            new ContactsClient(null, prefManager, activityClassManagerFactory, null);
            fail("Expected IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("twitter must not be null", e.getMessage());
        }
    }

    @Test
    public void testConstructor_nullPreferenceManager() throws Exception {
        try {
            new ContactsClient(twitterCore, null, activityClassManagerFactory, null);
            fail("Expected IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("preference manager must not be null", e.getMessage());
        }
    }

    @Test
    public void testConstructor_nullActivityClassManagerFactory() throws Exception {
        try {
            new ContactsClient(twitterCore, prefManager, null, null);
            fail("Expected IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("activityClassManagerFactory must not be null", e.getMessage());
        }
    }

    @Test
    public void testStartContactsUpload_noParams() {
        contactsClient = spy(contactsClient);

        contactsClient.startContactsUpload();

        verify(contactsClient).startContactsUpload(R.style.Digits_default);
    }

    @Test
    public void testStartContactsUpload_oneParam() {
        contactsClient = spy(contactsClient);

        contactsClient.startContactsUpload(R.style.Digits_default);

        verify(contactsClient).startContactsUpload(context, R.style.Digits_default);
    }

    @Test
    public void testStartContactsUpload_uploadPermissionNotGranted() {
        when(prefManager.hasContactImportPermissionGranted()).thenReturn(false);

        contactsClient.startContactsUpload(context, R.style.Digits_default);

        verify(context).startActivity(intentArgumentCaptor.capture());
        final Intent capturedIntent = intentArgumentCaptor.getValue();
        assertEquals(activityComponent, capturedIntent.getComponent());
        assertEquals(R.style.Digits_default,
                capturedIntent.getIntExtra(ThemeUtils.THEME_RESOURCE_ID, 0));
        assertEquals(Intent.FLAG_ACTIVITY_NEW_TASK, capturedIntent.getFlags());
        verify(prefManager).hasContactImportPermissionGranted();
    }

    @Test
    public void testStartContactsUpload_uploadPermissionGranted() {
        when(prefManager.hasContactImportPermissionGranted()).thenReturn(true);

        contactsClient.startContactsUpload(context, R.style.Digits_default);

        //verify start service is called, passing an ArgumentCaptor to get the intent and check
        // if it's correctly build
        verify(context).startService(intentArgumentCaptor.capture());
        final Intent capturedIntent = intentArgumentCaptor.getValue();
        assertEquals(serviceComponent, capturedIntent.getComponent());
        verify(prefManager).hasContactImportPermissionGranted();
    }

    @Test
    public void testHasUserGrantedPermission_uploadPermissionNotGranted() {
        when(prefManager.hasContactImportPermissionGranted()).thenReturn(false);

        assertFalse(contactsClient.hasUserGrantedPermission());
    }

    @Test
    public void testHasUserGrantedPermission_uploadPermissionGranted() {
        when(prefManager.hasContactImportPermissionGranted()).thenReturn(true);

        assertTrue(contactsClient.hasUserGrantedPermission());
    }

    @Test
    public void testDeleteAllContacts() {
        contactsClient.deleteAllUploadedContacts(callback);

        verify(contactsService).deleteAll(callback);
    }

    @Test
    public void testGetContactMatches() {
        final String cursor = "";
        final Integer count = 20;

        contactsClient.lookupContactMatches(cursor, count, callback);

        verify(contactsService).usersAndUploadedBy(cursor, count, callback);
    }

    @Test
    public void testGetContactMatches_countBelowMin() {
        final String cursor = "";
        final Integer count = 0;

        contactsClient.lookupContactMatches(cursor, count, callback);

        verify(contactsService).usersAndUploadedBy(cursor, null, callback);
    }

    @Test
    public void testGetContactMatches_countAboveMax() {
        final String cursor = "";
        final Integer count = 101;

        contactsClient.lookupContactMatches(cursor, count, callback);

        verify(contactsService).usersAndUploadedBy(cursor, null, callback);
    }

    @Test
    public void testGetContactMatches_countNull() {
        final String cursor = "";

        contactsClient.lookupContactMatches(cursor, null, callback);

        verify(contactsService).usersAndUploadedBy(cursor, null, callback);
    }

    @Test
    public void testUploadContacts() {
        final Vcards vCards = new Vcards(new ArrayList<String>());

        contactsClient.uploadContacts(vCards);

        verify(contactsService).upload(vCards);
    }
}
