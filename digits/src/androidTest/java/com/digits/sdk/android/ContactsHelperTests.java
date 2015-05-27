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

import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.test.MoreAsserts;
import android.test.mock.MockContentProvider;
import android.test.mock.MockContentResolver;
import android.test.mock.MockContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ContactsHelperTests extends DigitsAndroidTestCase {
    // Sample rows for matrix cursor
    private static final String[] COLUMNS = {"data1", "data2", "data3", "lookup", "mimetype",
            "is_primary"};
    private static final String[] PHONE_ROW = {"(555)555-5555", "2", "", "1",
            "vnd.android.cursor.item/phone_v2", "0"};
    private static final String[] EMAIL_ROW = {"support@digits.com", "3", "", "1",
            "vnd.android.cursor.item/email_v2", "1"};
    private static final String[] NAME_ROW = {"nene goose", "nene", "goose", "1",
            "vnd.android.cursor.item/name", ""};

    // Expected results from sample cursor
    private static final String SAMPLE_CARD = "BEGIN:VCARD\r\nVERSION:3.0\r\nN:goose;nene;;;" +
            "\r\nFN:nene goose\r\nTEL;TYPE=CELL:555-555-5555\r\nEMAIL;TYPE=PREF:support@digits" +
            ".com\r\nEND:VCARD\r\n";

    private MockContext context;
    private MockContentResolver contentResolver;
    private MockContentProvider provider;
    private Cursor cursor;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        context = mock(MockContext.class);
        contentResolver = new MockContentResolver();
        cursor = createCursor();
        provider = new MockContentProvider() {
            @Override
            public Cursor query(Uri uri, String[] projection, String selection,
                                String[] selectionArgs, String sortOrder) {
                assertEquals(ContactsContract.AUTHORITY, uri.getAuthority());
                MoreAsserts.assertContentsInAnyOrder(Arrays.asList(COLUMNS), (Object[]) projection);
                assertEquals(null, sortOrder);

                return cursor;
            }
        };
        contentResolver.addProvider(ContactsContract.AUTHORITY, provider);

        when(context.getContentResolver()).thenReturn(contentResolver);
    }

    static Cursor createCursor() {
        final MatrixCursor matrixCursor = new MatrixCursor(COLUMNS);
        matrixCursor.addRow(PHONE_ROW);
        matrixCursor.addRow(EMAIL_ROW);
        matrixCursor.addRow(NAME_ROW);
        return matrixCursor;
    }

    static ArrayList<String> createCardList() {
        final ArrayList<String> vCards = new ArrayList<>();
        vCards.add(SAMPLE_CARD);
        return vCards;
    }

    public void testGetContactsCursor() {
        final ContactsHelper contactsHelper = new ContactsHelper(context);
        final Cursor cursor = contactsHelper.getContactsCursor();

        verify(context).getContentResolver();

        assertEquals(COLUMNS, cursor.getColumnNames());
    }

    public void testCreateContactList() {
        final ContactsHelper contactsHelper = new ContactsHelper(context);

        final List<String> cards = contactsHelper.createContactList(cursor);

        assertEquals(1, cards.size());
        MoreAsserts.assertEquals(createCardList().toArray(), cards.toArray());
    }
}
