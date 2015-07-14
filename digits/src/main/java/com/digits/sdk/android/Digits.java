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

import android.annotation.TargetApi;
import android.os.Build;

import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.Kit;
import io.fabric.sdk.android.services.concurrency.DependsOn;
import io.fabric.sdk.android.services.persistence.PreferenceStoreImpl;

import com.twitter.sdk.android.core.PersistedSessionManager;
import com.twitter.sdk.android.core.Session;
import com.twitter.sdk.android.core.SessionManager;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.internal.MigrationHelper;
import com.twitter.sdk.android.core.internal.SessionMonitor;
import com.twitter.sdk.android.core.internal.TwitterSessionVerifier;
import com.twitter.sdk.android.core.internal.scribe.DefaultScribeClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Digits allows authentication based on a phone number.
 */
@DependsOn(TwitterCore.class)
public class Digits extends Kit<Void> {
    public static final String TAG = "Digits";

    static final String PREF_KEY_ACTIVE_SESSION = "active_session";
    static final String PREF_KEY_SESSION = "session";
    static final String SESSION_PREF_FILE_NAME = "session_store";

    private static final String KIT_SCRIBE_NAME = "Digits";

    private volatile DigitsClient digitsClient;
    private volatile ContactsClient contactsClient;
    private SessionManager<DigitsSession> sessionManager;
    private SessionMonitor<DigitsSession> sessionMonitor;
    private ActivityClassManager activityClassManager;
    private DigitsScribeService scribeService;


    private int themeResId;

    public static Digits getInstance() {
        return Fabric.getKit(Digits.class);
    }

    /**
     * Starts the authentication flow
     *
     * @param callback will get the success or failure callback. It can be null,
     * but the developer will not get any callback.
     */
    public static void authenticate(AuthCallback callback) {
        authenticate(callback, ThemeUtils.DEFAULT_THEME);
    }

    /**
     * Starts the authentication flow with the provided phone number.
     *
     * @param callback will get the success or failure callback. It can be null,
     * but the developer will not get any callback.
     * @param phoneNumber the phone number to authenticate
     */
    public static void authenticate(AuthCallback callback, String phoneNumber) {
        authenticate(callback, ThemeUtils.DEFAULT_THEME, phoneNumber);
    }

    /**
     * Starts and sets the theme for the authentication flow.
     *
     * @param callback will get the success or failure callback. It can be null,
     * but the developer will not get any callback.
     * @param themeResId Theme resource id
     */
    public static void authenticate(AuthCallback callback, int themeResId) {
        getInstance().setTheme(themeResId);
        getInstance().getDigitsClient().startSignUp(callback);
    }

    /**
     * Starts the authentication flow with the provided phone number and theme.
     *
     * @param callback will get the success or failure callback. It can be null,
     * but the developer will not get any callback.
     * @param themeResId Theme resource id
     * @param phoneNumber the phone number to authenticate
     */
    public static void authenticate(AuthCallback callback, int themeResId, String phoneNumber) {
        getInstance().setTheme(themeResId);
        getInstance().getDigitsClient().startSignUp(callback, phoneNumber);
    }

    public static SessionManager<DigitsSession> getSessionManager() {
        return getInstance().sessionManager;
    }

    public Digits() {
        super();
        scribeService = new NoOpScribeService();
    }

    @Override
    public String getVersion() {
        return BuildConfig.VERSION_NAME + "." + BuildConfig.BUILD_NUMBER;
    }

    @Override
    protected boolean onPreExecute() {
        final MigrationHelper migrationHelper = new MigrationHelper();
        migrationHelper.migrateSessionStore(getContext(), getIdentifier(),
                getIdentifier() + ":" + SESSION_PREF_FILE_NAME + ".xml");

        sessionManager = new PersistedSessionManager<>(new PreferenceStoreImpl(getContext(),
                SESSION_PREF_FILE_NAME), new DigitsSession.Serializer(), PREF_KEY_ACTIVE_SESSION,
                PREF_KEY_SESSION);

        sessionMonitor = new SessionMonitor<>(sessionManager, getExecutorService(),
                new TwitterSessionVerifier());
        return super.onPreExecute();
    }

    @Override
    protected Void doInBackground() {
        // Trigger restoration of session
        sessionManager.getActiveSession();
        createDigitsClient();
        createContactsClient();
        scribeService = new DigitsScribeServiceImp(setUpScribing());
        sessionMonitor.triggerVerificationIfNecessary();
        // Monitor activity lifecycle after sessions have been restored. Otherwise we would not
        // have any sessions to monitor anyways.
        sessionMonitor.monitorActivityLifecycle(getFabric().getActivityLifecycleManager());
        return null;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    int getTheme() {
        if (themeResId != ThemeUtils.DEFAULT_THEME) {
            return themeResId;
        }

        return R.style.Digits_default;
    }

    protected void setTheme(int themeResId) {
        this.themeResId = themeResId;
        createActivityClassManager();
    }

    @Override
    public String getIdentifier() {
        return BuildConfig.GROUP + ":" + BuildConfig.ARTIFACT_ID;
    }

    DigitsClient getDigitsClient() {
        if (digitsClient == null) {
            createDigitsClient();
        }
        return digitsClient;
    }

    protected DigitsScribeService getScribeService() {
        return scribeService;
    }

    private synchronized void createDigitsClient() {
        if (digitsClient == null) {
            digitsClient = new DigitsClient();
        }
    }

    public ContactsClient getContactsClient() {
        if (contactsClient == null) {
            createContactsClient();
        }
        return contactsClient;
    }

    private synchronized void createContactsClient() {
        if (contactsClient == null) {
            contactsClient = new ContactsClient();
        }
    }

    protected ExecutorService getExecutorService() {
        return getFabric().getExecutorService();
    }

    private DefaultScribeClient setUpScribing() {
        final List<SessionManager<? extends Session>> sessionManagers = new ArrayList<>();
        sessionManagers.add(sessionManager);
        return new DefaultScribeClient(this, KIT_SCRIBE_NAME,
                sessionManagers, getIdManager());
    }

    protected ActivityClassManager getActivityClassManager() {
        if (activityClassManager == null) {
            createActivityClassManager();
        }
        return activityClassManager;
    }

    protected void createActivityClassManager() {
        final ActivityClassManagerFactory factory = new ActivityClassManagerFactory();
        activityClassManager = factory.createActivityClassManager(getContext(), themeResId);
    }

    /**
     * Exposes the AuthConfig used in this instance of Digits kit
     */
    public TwitterAuthConfig getAuthConfig() {
        return TwitterCore.getInstance().getAuthConfig();
    }
}
