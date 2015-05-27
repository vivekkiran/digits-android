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

import com.twitter.sdk.android.core.AuthenticatedClient;
import com.twitter.sdk.android.core.TwitterCore;

import retrofit.RestAdapter;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

public class ContactsClient {
    public static final int MAX_PAGE_SIZE = 100;
    private final TwitterCore twitterCore;
    private final ContactsPreferenceManager prefManager;
    private ContactsService contactsService;
    private ActivityClassManagerFactory activityClassManagerFactory;

    ContactsClient() {
        this(TwitterCore.getInstance(), new ContactsPreferenceManager(),
                new ActivityClassManagerFactory(), null);
    }

    ContactsClient(TwitterCore twitterCore, ContactsPreferenceManager prefManager,
            ActivityClassManagerFactory activityClassManagerFactory,
            ContactsService contactsService) {
        if (twitterCore == null) {
            throw new IllegalArgumentException("twitter must not be null");
        }
        if (prefManager == null) {
            throw new IllegalArgumentException("preference manager must not be null");
        }
        if (activityClassManagerFactory == null) {
            throw new IllegalArgumentException("activityClassManagerFactory must not be null");
        }
        this.twitterCore = twitterCore;
        this.prefManager = prefManager;
        this.activityClassManagerFactory = activityClassManagerFactory;
        this.contactsService = contactsService;
    }

    /**
     * First checks if user previously gave permission to upload contacts. If not, shows
     * dialog requesting permission to upload users contacts. If permission granted start
     * background service to upload contacts. Otherwise, do nothing.
     */
    public void startContactsUpload() {
        startContactsUpload(R.style.Digits_default);
    }

    /**
     * First checks if user previously gave permission to upload contacts. If not, shows
     * dialog requesting permission to upload users contacts. If permission granted start
     * background service to upload contacts. Otherwise, do nothing.
     *
     * @param themeResId Resource id of theme
     *
     */
    public void startContactsUpload(int themeResId) {
        startContactsUpload(twitterCore.getContext(), themeResId);
    }

    /**
     * Returns true if user has previously granted contacts upload permission. Otherwise, returns
     * false.
     */
    public boolean hasUserGrantedPermission() {
        return prefManager.hasContactImportPermissionGranted();
    }

    protected void startContactsUpload(Context context, int themeResId) {
        if (!hasUserGrantedPermission()) {
            startContactsActivity(context, themeResId);
        } else {
            startContactsService(context);
        }
    }

    private void startContactsActivity(Context context, int themeResId) {
        final ActivityClassManager activityClassManager =
                activityClassManagerFactory.createActivityClassManager(context, themeResId);
        final Intent intent = new Intent(context, activityClassManager.getContactsActivity());
        intent.putExtra(ThemeUtils.THEME_RESOURCE_ID, themeResId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void startContactsService(Context context) {
        context.startService(new Intent(context, ContactsUploadService.class));
    }

    private ContactsService getContactsService() {
        if (contactsService != null) {
            return contactsService;
        }

        final RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(new DigitsApi().getBaseHostUrl())
                .setClient(new AuthenticatedClient(twitterCore.getAuthConfig(),
                        Digits.getSessionManager().getActiveSession(),
                        twitterCore.getSSLSocketFactory()))
                .build();

        contactsService = adapter.create(ContactsService.class);
        return contactsService;
    }

    /**
     * Deletes all uploaded contacts.
     *
     * @param callback to be executed on UI thread with HTTP response.
     */
    public void deleteAllUploadedContacts(final ContactsCallback<Response> callback) {
        getContactsService().deleteAll(callback);
    }

    /**
     * Lookup matched contacts.
     *
     * @param nextCursor reference to next set of results. If null returns the first 100 users.
     * @param count      number of results to return. Min value is 1. Max value is 100. Default
     *                   value is 50. Values out of range will return default.
     * @param callback   to be executed on UI thread with matched users.
     */
    public void lookupContactMatches(final String nextCursor, final Integer count,
            final ContactsCallback<Contacts> callback) {
        if (count == null || count < 1 || count > 100) {
            getContactsService().usersAndUploadedBy(nextCursor, null, callback);
        } else {
            getContactsService().usersAndUploadedBy(nextCursor, count, callback);
        }
    }

    UploadResponse uploadContacts(Vcards vcards) {
        return getContactsService().upload(vcards);
    }

    interface ContactsService {
        @POST("/1.1/contacts/upload.json")
        UploadResponse upload(@Body Vcards vcards);

        @POST("/1.1/contacts/destroy/all.json")
        void deleteAll(ContactsCallback<Response> cb);

        @GET("/1.1/contacts/users_and_uploaded_by.json")
        void usersAndUploadedBy(@Query("next_cursor") String nextCursor,
                @Query("count") Integer count, ContactsCallback<Contacts> cb);
    }

}
