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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class CountryListAdapterTests {
    private CountryListAdapter countryListAdapter;

    @Before
    public void setUp() throws Exception {
        final ArrayList<CountryInfo> countries = new ArrayList<>();
        countries.add(new CountryInfo("Germany", 1));
        countries.add(new CountryInfo("Saoma", 2));
        countries.add(new CountryInfo("spain", 3));
        countries.add(new CountryInfo("United States", 4));

        countryListAdapter = new CountryListAdapter(RuntimeEnvironment.application);
        countryListAdapter.setData(countries);
    }

    @Test
    public void testGetSections() {
        assertEquals(3, countryListAdapter.getSections().length);
        assertEquals("G", countryListAdapter.getSections()[0]);
        assertEquals("S", countryListAdapter.getSections()[1]);
        assertEquals("U", countryListAdapter.getSections()[2]);
    }

    @Test
    public void testGetPositionForSection() {
        assertEquals(0, countryListAdapter.getPositionForSection(-1));
        assertEquals(0, countryListAdapter.getPositionForSection(0));
        assertEquals(1, countryListAdapter.getPositionForSection(1));
        assertEquals(3, countryListAdapter.getPositionForSection(2));
        assertEquals(3, countryListAdapter.getPositionForSection(3));
    }
}
