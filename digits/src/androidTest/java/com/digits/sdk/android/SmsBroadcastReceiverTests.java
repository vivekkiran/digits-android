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
import android.telephony.SmsMessage;
import android.widget.EditText;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SmsBroadcastReceiverTests extends DigitsAndroidTestCase {
    final String TEST_CODE = "635589";
    final String TEST_MESSAGE =
            "40404 - Confirmation code: 635589. Enter this code in your app. (Digits by Twitter)";
    byte[] pdu = {7, -111, 65, 64, 84, 5, 0, -8, 4, 11, -111, -111, 97, 117, 50, 21, -8, 0, 0,
            81, 32, 48, -127, 4, 82, 10, 83, 52, 24, 13, 70, 3, -75, 64, -61, -73, -37, -100,
            -106, -73, -61, -12, -12, -37, 13, 26, -65, -55, 101, 29, -56, 54, -85, -43, 112, 57,
            23, -88, -24, -90, -105, -27, 32, 58, 58, 61, 7, -115, -33, -28, 50, 40, -19, 6, -27,
            -33, 117, 57, 40, 12, -121, -69, 64, 40, 98, -6, -100, -90, -49, 65, -30, 60, -120,
            122, 79, -45, -23, 101, 121, 10};
    SmsBroadcastReceiver receiver;
    EditText editText;
    SmsMessage sms;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        sms = mock(SmsMessage.class);
        editText = new EditText(getContext());
        receiver = new SmsBroadcastReceiver(editText);
    }

    public void testGetConfirmationCode_validMessage() {
        when(sms.getDisplayMessageBody()).thenReturn(TEST_MESSAGE);

        assertEquals(TEST_CODE, receiver.getConfirmationCode(sms));

        verify(sms).getDisplayMessageBody();
    }

    public void testGetConfirmationCode_nullMessage() {
        assertNull(receiver.getConfirmationCode(sms));

        verify(sms).getDisplayMessageBody();
    }

    public void testGetConfirmationCode_validMessageInList() {
        when(sms.getDisplayMessageBody()).thenReturn(TEST_MESSAGE);

        final SmsMessage[] messages = new SmsMessage[2];
        messages[0] = mock(SmsMessage.class);
        messages[1] = sms;

        assertEquals(TEST_CODE, receiver.getConfirmationCode(messages));

        verify(sms).getDisplayMessageBody();
    }

    public void testGetMessagesFromIntent() {
        final Intent intent = mock(Intent.class);
        when(intent.getSerializableExtra(SmsBroadcastReceiver.PDU_EXTRA)).thenReturn(new
                Object[]{pdu});

        final SmsMessage[] messages = receiver.getMessagesFromIntent(intent);

        assertEquals(1, messages.length);
        assertEquals(TEST_MESSAGE, messages[0].getDisplayMessageBody());
    }

    public void testOnReceive() {
        final Intent intent = mock(Intent.class);
        when(intent.getSerializableExtra(SmsBroadcastReceiver.PDU_EXTRA)).thenReturn(new
                Object[]{pdu});

        receiver.onReceive(getContext(), intent);

        assertEquals(TEST_CODE, editText.getText().toString());
        assertEquals(TEST_CODE.length(), editText.getSelectionEnd());
    }
}
