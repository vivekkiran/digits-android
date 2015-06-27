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

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class ContactsActionBarActivity extends AppCompatActivity {

    ContactsActivityDelegateImpl delegate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(getIntent().getIntExtra(ThemeUtils.THEME_RESOURCE_ID,
                R.style.Theme_AppCompat_Light));
        super.onCreate(savedInstanceState);

        delegate = new ContactsActivityDelegateImpl(this);
        delegate.init();
    }
}
