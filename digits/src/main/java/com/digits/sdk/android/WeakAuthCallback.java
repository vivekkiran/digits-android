package com.digits.sdk.android;

import java.lang.ref.WeakReference;

class WeakAuthCallback implements AuthCallback {
    private final WeakReference<AuthCallback> callbackWeakReference;

    public WeakAuthCallback(AuthCallback callback) {
        this.callbackWeakReference = new WeakReference<>(callback);
    }

    @Override
    public void success(DigitsSession session, String phoneNumber) {
        final AuthCallback callback = callbackWeakReference.get();
        if (callback != null) {
            callback.success(session, phoneNumber);
        }
    }

    @Override
    public void failure(DigitsException error) {
        final AuthCallback callback = callbackWeakReference.get();
        if (callback != null) {
            callback.failure(error);
        }
    }

    public AuthCallback getCallback() {
        return callbackWeakReference.get();
    }
}
