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

/**
 * <p/>
 * Construct using DigitsAuthConfig.Builder {@link DigitsAuthConfig.Builder}
 * <p/>
 */
public class DigitsAuthConfig {
    protected final boolean isEmailRequired;
    protected final int themeResId;
    protected final String phoneNumber;
    protected final AuthCallback authCallback;

    protected DigitsAuthConfig(boolean isEmailRequired, String phoneNumber,
                     AuthCallback authCallback, int themeResId){
        this.isEmailRequired = isEmailRequired;
        this.themeResId = themeResId;
        this.phoneNumber = phoneNumber;
        this.authCallback = authCallback;
    }

    /**
     * Digits Auth Config Builder
     * <p/>
     * Used to build a Digits Auth Config object {@link DigitsAuthConfig}
     * Use {@link #withAuthCallBack} followed by {@link #build()}
     * <pre>
     * // Example
     * Builder digitsAuthConfigBuilder = new DigitsAuthConfig.Builder()
     *       .withAuthCallBack(callback)
     *       .withPhoneNumber("+12345677832")
     *       .withEmailCollection()
     *       .withThemeResId(R.style.LightTheme);
     * Digits.authenticate(digitsAuthConfigBuilder.build());
     * </pre>
     * <p/>
     */
    public static class Builder {
        boolean isEmailRequired;
        String phoneNumber;
        AuthCallback authCallback;
        int themeResId;

        /**
         * Construct {@link DigitsAuthConfig.Builder}
         */
        public Builder() {
            this.isEmailRequired = false;
            this.themeResId = ThemeUtils.DEFAULT_THEME;
        }

        public Builder(Builder that){
            this.isEmailRequired = that.isEmailRequired;
            this.phoneNumber = that.phoneNumber;
            this.authCallback = that.authCallback;
            this.themeResId = that.themeResId;
        }

        /**
         * Turns on email collection.
         */
        public Builder withEmailCollection(){
            this.isEmailRequired = true;
            return this;
        }

        /**
         * Turns email collection on/off
         * @param collectEmail Should collect email in the auth flow
         */
        public Builder withEmailCollection(boolean collectEmail){
            this.isEmailRequired = collectEmail;
            return this;
        }

        /**
         * Set default phoneNumber.
         * @param phoneNumber the phone number to authenticate
         */
        public Builder withPhoneNumber(String phoneNumber){
            this.phoneNumber = phoneNumber;
            return this;

        }

        /**
         * Set auth callback.
         * @param authCallback {@link AuthCallback} to be called with the authentication result, or <code>null</code> if no callback is needed.
         *                    Digits holds a weak reference to this object, therefore the caller should
         *                    <strong>have a strong reference to this object</strong> or it will never receive
         *                    the result.
         */
        public Builder withAuthCallBack(AuthCallback authCallback){
            this.authCallback = authCallback;
            return this;
        }

        /**
         * Set theme resource id.
         * @param themeResId Theme resource id
         */
        public Builder withThemeResId(int themeResId){
            this.themeResId = themeResId;
            return this;
        }

        /**
         * Returns DigitsAuthConfig constructed using the builder.
         */
        public DigitsAuthConfig build() {
            if (authCallback == null) {
                throw new IllegalArgumentException("AuthCallback must not be null");
            }

            return new DigitsAuthConfig(isEmailRequired,
                    phoneNumber == null ? "": phoneNumber, authCallback, themeResId);
        }
    }
}
