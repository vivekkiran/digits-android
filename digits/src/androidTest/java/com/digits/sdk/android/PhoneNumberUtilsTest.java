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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PhoneNumberUtilsTest extends DigitsAndroidTestCase {
    private static final String INVENTED_ISO = "random";
    private SimManager simManager;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        simManager = mock(SimManagerTest.DummySimManager.class);
    }

    public void testGetPhoneNumber_nullSim() throws Exception {
        final DummyPhoneNumberUtils DummyPhoneNumberUtils = new DummyPhoneNumberUtils(null);
        assertEquals(PhoneNumber.emptyPhone(), DummyPhoneNumberUtils.getPhoneNumber());
    }

    public void testGetPhoneNumber() throws Exception {
        when(simManager.getCountryIso()).thenReturn(US_ISO2);
        when(simManager.getRawPhoneNumber()).thenReturn(RAW_PHONE);
        final DummyPhoneNumberUtils DummyPhoneNumberUtils = new DummyPhoneNumberUtils(simManager);
        final PhoneNumber number = DummyPhoneNumberUtils.getPhoneNumber();
        verify(simManager).getCountryIso();
        verify(simManager).getRawPhoneNumber();
        assertEquals(PHONE_NO_COUNTRY_CODE, number.getPhoneNumber());
        assertEquals(US_COUNTRY_CODE, number.getCountryCode());
        assertEquals(US_ISO2, number.getCountryIso());
    }

    public void testGetPhoneNumber_noCountryCode() throws Exception {
        when(simManager.getCountryIso()).thenReturn(US_ISO2);
        when(simManager.getRawPhoneNumber()).thenReturn(PHONE_NO_COUNTRY_CODE);
        final DummyPhoneNumberUtils DummyPhoneNumberUtils = new DummyPhoneNumberUtils(simManager);
        final PhoneNumber number = DummyPhoneNumberUtils.getPhoneNumber();
        verify(simManager).getCountryIso();
        verify(simManager).getRawPhoneNumber();
        assertEquals(PHONE_NO_COUNTRY_CODE, number.getPhoneNumber());
        assertEquals(US_COUNTRY_CODE, number.getCountryCode());
        assertEquals(US_ISO2, number.getCountryIso());
    }

    public void testGetPhoneNumber_noPlusSymbol() throws Exception {
        when(simManager.getCountryIso()).thenReturn(US_ISO2);
        when(simManager.getRawPhoneNumber()).thenReturn(PHONE);
        final DummyPhoneNumberUtils DummyPhoneNumberUtils = new DummyPhoneNumberUtils(simManager);
        final PhoneNumber number = DummyPhoneNumberUtils.getPhoneNumber();
        verify(simManager).getCountryIso();
        verify(simManager).getRawPhoneNumber();
        assertEquals(PHONE_NO_COUNTRY_CODE, number.getPhoneNumber());
        assertEquals(US_COUNTRY_CODE, number.getCountryCode());
        assertEquals(US_ISO2, number.getCountryIso());
    }

    public void testGetPhoneNumber_plusSymbolNoCountryCode() throws Exception {
        when(simManager.getCountryIso()).thenReturn(US_ISO2);
        when(simManager.getRawPhoneNumber()).thenReturn(PHONE_PLUS_SYMBOL_NO_COUNTRY_CODE);
        final DummyPhoneNumberUtils DummyPhoneNumberUtils = new DummyPhoneNumberUtils(simManager);
        final PhoneNumber number = DummyPhoneNumberUtils.getPhoneNumber();
        verify(simManager).getCountryIso();
        verify(simManager).getRawPhoneNumber();
        assertEquals(PHONE_NO_COUNTRY_CODE, number.getPhoneNumber());
        assertEquals(US_COUNTRY_CODE, number.getCountryCode());
        assertEquals(US_ISO2, number.getCountryIso());
    }

    public void testGetPhoneNumber_nonMatchingISO() throws Exception {
        when(simManager.getCountryIso()).thenReturn(INVENTED_ISO);
        when(simManager.getRawPhoneNumber()).thenReturn(RAW_PHONE);
        final DummyPhoneNumberUtils DummyPhoneNumberUtils = new DummyPhoneNumberUtils(simManager);
        final PhoneNumber number = DummyPhoneNumberUtils.getPhoneNumber();
        verify(simManager).getCountryIso();
        verify(simManager).getRawPhoneNumber();
        assertEquals(PHONE, number.getPhoneNumber());
        assertEquals("", number.getCountryCode());
        assertEquals(INVENTED_ISO, number.getCountryIso());
    }

    public class DummyPhoneNumberUtils extends PhoneNumberUtils{


        DummyPhoneNumberUtils(SimManager simManager) {
            super(simManager);
        }
    }
}
