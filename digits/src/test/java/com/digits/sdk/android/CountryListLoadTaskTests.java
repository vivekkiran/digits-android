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
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class CountryListLoadTaskTests {
    private static final ArrayList<CountryInfo> COUNTRY_LIST = new ArrayList<>();

    static {
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "AF").getDisplayCountry(), 93));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "AX").getDisplayCountry(), 358));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "AL").getDisplayCountry(), 355));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "DZ").getDisplayCountry(), 213));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "AS").getDisplayCountry(), 1));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "AD").getDisplayCountry(), 376));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "AO").getDisplayCountry(), 244));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "AI").getDisplayCountry(), 1));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "AG").getDisplayCountry(), 1));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "AR").getDisplayCountry(), 54));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "AM").getDisplayCountry(), 374));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "AW").getDisplayCountry(), 297));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "AC").getDisplayCountry(), 247));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "AU").getDisplayCountry(), 61));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "AT").getDisplayCountry(), 43));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "AZ").getDisplayCountry(), 994));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "BS").getDisplayCountry(), 1));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "BH").getDisplayCountry(), 973));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "BD").getDisplayCountry(), 880));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "BB").getDisplayCountry(), 1));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "BY").getDisplayCountry(), 375));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "BE").getDisplayCountry(), 32));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "BZ").getDisplayCountry(), 501));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "BJ").getDisplayCountry(), 229));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "BM").getDisplayCountry(), 1));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "BT").getDisplayCountry(), 975));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "BO").getDisplayCountry(), 591));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "BA").getDisplayCountry(), 387));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "BW").getDisplayCountry(), 267));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "BR").getDisplayCountry(), 55));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "IO").getDisplayCountry(), 246));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "VG").getDisplayCountry(), 1));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "BN").getDisplayCountry(), 673));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "BG").getDisplayCountry(), 359));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "BF").getDisplayCountry(), 226));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "BI").getDisplayCountry(), 257));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "KH").getDisplayCountry(), 855));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "CM").getDisplayCountry(), 237));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "CA").getDisplayCountry(), 1));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "CV").getDisplayCountry(), 238));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "BQ").getDisplayCountry(), 599));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "KY").getDisplayCountry(), 1));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "CF").getDisplayCountry(), 236));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "TD").getDisplayCountry(), 235));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "CL").getDisplayCountry(), 56));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "CN").getDisplayCountry(), 86));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "CX").getDisplayCountry(), 61));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "CC").getDisplayCountry(), 61));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "CO").getDisplayCountry(), 57));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "KM").getDisplayCountry(), 269));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "CD").getDisplayCountry(), 243));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "CG").getDisplayCountry(), 242));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "CK").getDisplayCountry(), 682));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "CR").getDisplayCountry(), 506));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "CI").getDisplayCountry(), 225));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "HR").getDisplayCountry(), 385));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "CU").getDisplayCountry(), 53));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "CW").getDisplayCountry(), 599));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "CY").getDisplayCountry(), 357));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "CZ").getDisplayCountry(), 420));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "DK").getDisplayCountry(), 45));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "DJ").getDisplayCountry(), 253));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "DM").getDisplayCountry(), 1));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "DO").getDisplayCountry(), 1));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "TL").getDisplayCountry(), 670));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "EC").getDisplayCountry(), 593));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "EG").getDisplayCountry(), 20));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "SV").getDisplayCountry(), 503));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "GQ").getDisplayCountry(), 240));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "ER").getDisplayCountry(), 291));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "EE").getDisplayCountry(), 372));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "ET").getDisplayCountry(), 251));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "FK").getDisplayCountry(), 500));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "FO").getDisplayCountry(), 298));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "FJ").getDisplayCountry(), 679));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "FI").getDisplayCountry(), 358));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "FR").getDisplayCountry(), 33));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "GF").getDisplayCountry(), 594));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "PF").getDisplayCountry(), 689));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "GA").getDisplayCountry(), 241));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "GM").getDisplayCountry(), 220));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "GE").getDisplayCountry(), 995));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "DE").getDisplayCountry(), 49));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "GH").getDisplayCountry(), 233));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "GI").getDisplayCountry(), 350));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "GR").getDisplayCountry(), 30));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "GL").getDisplayCountry(), 299));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "GD").getDisplayCountry(), 1));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "GP").getDisplayCountry(), 590));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "GU").getDisplayCountry(), 1));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "GT").getDisplayCountry(), 502));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "GG").getDisplayCountry(), 44));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "GN").getDisplayCountry(), 224));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "GW").getDisplayCountry(), 245));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "GY").getDisplayCountry(), 592));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "HT").getDisplayCountry(), 509));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "HM").getDisplayCountry(), 672));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "HN").getDisplayCountry(), 504));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "HK").getDisplayCountry(), 852));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "HU").getDisplayCountry(), 36));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "IS").getDisplayCountry(), 354));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "IN").getDisplayCountry(), 91));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "ID").getDisplayCountry(), 62));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "IR").getDisplayCountry(), 98));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "IQ").getDisplayCountry(), 964));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "IE").getDisplayCountry(), 353));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "IM").getDisplayCountry(), 44));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "IL").getDisplayCountry(), 972));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "IT").getDisplayCountry(), 39));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "JM").getDisplayCountry(), 1));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "JP").getDisplayCountry(), 81));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "JE").getDisplayCountry(), 44));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "JO").getDisplayCountry(), 962));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "KZ").getDisplayCountry(), 7));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "KE").getDisplayCountry(), 254));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "KI").getDisplayCountry(), 686));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "XK").getDisplayCountry(), 381));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "KW").getDisplayCountry(), 965));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "KG").getDisplayCountry(), 996));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "LA").getDisplayCountry(), 856));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "LV").getDisplayCountry(), 371));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "LB").getDisplayCountry(), 961));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "LS").getDisplayCountry(), 266));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "LR").getDisplayCountry(), 231));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "LY").getDisplayCountry(), 218));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "LI").getDisplayCountry(), 423));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "LT").getDisplayCountry(), 370));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "LU").getDisplayCountry(), 352));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "MO").getDisplayCountry(), 853));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "MK").getDisplayCountry(), 389));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "MG").getDisplayCountry(), 261));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "MW").getDisplayCountry(), 265));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "MY").getDisplayCountry(), 60));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "MV").getDisplayCountry(), 960));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "ML").getDisplayCountry(), 223));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "MT").getDisplayCountry(), 356));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "MH").getDisplayCountry(), 692));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "MQ").getDisplayCountry(), 596));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "MR").getDisplayCountry(), 222));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "MU").getDisplayCountry(), 230));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "YT").getDisplayCountry(), 262));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "MX").getDisplayCountry(), 52));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "FM").getDisplayCountry(), 691));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "MD").getDisplayCountry(), 373));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "MC").getDisplayCountry(), 377));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "MN").getDisplayCountry(), 976));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "ME").getDisplayCountry(), 382));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "MS").getDisplayCountry(), 1));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "MA").getDisplayCountry(), 212));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "MZ").getDisplayCountry(), 258));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "MM").getDisplayCountry(), 95));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "NA").getDisplayCountry(), 264));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "NR").getDisplayCountry(), 674));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "NP").getDisplayCountry(), 977));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "NL").getDisplayCountry(), 31));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "NC").getDisplayCountry(), 687));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "NZ").getDisplayCountry(), 64));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "NI").getDisplayCountry(), 505));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "NE").getDisplayCountry(), 227));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "NG").getDisplayCountry(), 234));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "NU").getDisplayCountry(), 683));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "NF").getDisplayCountry(), 672));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "KP").getDisplayCountry(), 850));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "MP").getDisplayCountry(), 1));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "NO").getDisplayCountry(), 47));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "OM").getDisplayCountry(), 968));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "PK").getDisplayCountry(), 92));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "PW").getDisplayCountry(), 680));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "PS").getDisplayCountry(), 970));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "PA").getDisplayCountry(), 507));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "PG").getDisplayCountry(), 675));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "PY").getDisplayCountry(), 595));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "PE").getDisplayCountry(), 51));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "PH").getDisplayCountry(), 63));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "PL").getDisplayCountry(), 48));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "PT").getDisplayCountry(), 351));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "PR").getDisplayCountry(), 1));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "QA").getDisplayCountry(), 974));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "RE").getDisplayCountry(), 262));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "RO").getDisplayCountry(), 40));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "RU").getDisplayCountry(), 7));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "RW").getDisplayCountry(), 250));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "BL").getDisplayCountry(), 590));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "SH").getDisplayCountry(), 290));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "KN").getDisplayCountry(), 1));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "LC").getDisplayCountry(), 1));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "MF").getDisplayCountry(), 590));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "PM").getDisplayCountry(), 508));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "VC").getDisplayCountry(), 1));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "WS").getDisplayCountry(), 685));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "SM").getDisplayCountry(), 378));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "ST").getDisplayCountry(), 239));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "SA").getDisplayCountry(), 966));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "SN").getDisplayCountry(), 221));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "RS").getDisplayCountry(), 381));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "SC").getDisplayCountry(), 248));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "SL").getDisplayCountry(), 232));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "SG").getDisplayCountry(), 65));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "SX").getDisplayCountry(), 1));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "SK").getDisplayCountry(), 421));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "SI").getDisplayCountry(), 386));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "SB").getDisplayCountry(), 677));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "SO").getDisplayCountry(), 252));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "ZA").getDisplayCountry(), 27));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "GS").getDisplayCountry(), 500));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "KR").getDisplayCountry(), 82));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "SS").getDisplayCountry(), 211));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "ES").getDisplayCountry(), 34));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "LK").getDisplayCountry(), 94));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "SD").getDisplayCountry(), 249));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "SR").getDisplayCountry(), 597));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "SJ").getDisplayCountry(), 47));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "SZ").getDisplayCountry(), 268));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "SE").getDisplayCountry(), 46));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "CH").getDisplayCountry(), 41));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "SY").getDisplayCountry(), 963));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "TW").getDisplayCountry(), 886));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "TJ").getDisplayCountry(), 992));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "TZ").getDisplayCountry(), 255));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "TH").getDisplayCountry(), 66));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "TG").getDisplayCountry(), 228));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "TK").getDisplayCountry(), 690));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "TO").getDisplayCountry(), 676));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "TT").getDisplayCountry(), 1));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "TN").getDisplayCountry(), 216));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "TR").getDisplayCountry(), 90));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "TM").getDisplayCountry(), 993));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "TC").getDisplayCountry(), 1));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "TV").getDisplayCountry(), 688));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "VI").getDisplayCountry(), 1));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "UG").getDisplayCountry(), 256));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "UA").getDisplayCountry(), 380));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "AE").getDisplayCountry(), 971));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "GB").getDisplayCountry(), 44));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "US").getDisplayCountry(), 1));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "UY").getDisplayCountry(), 598));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "UZ").getDisplayCountry(), 998));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "VU").getDisplayCountry(), 678));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "VA").getDisplayCountry(), 379));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "VE").getDisplayCountry(), 58));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "VN").getDisplayCountry(), 84));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "WF").getDisplayCountry(), 681));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "EH").getDisplayCountry(), 212));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "YE").getDisplayCountry(), 967));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "ZM").getDisplayCountry(), 260));
        COUNTRY_LIST.add(new CountryInfo(new Locale("", "ZW").getDisplayCountry(), 263));
    }

    private CountryListLoadTask task;
    private CountryListLoadTask.Listener listener;

    @Before
    public void setUp() throws Exception {
        // Create task and mock dependencies
        listener = mock(CountryListLoadTask.Listener.class);
        task = new CountryListLoadTask(listener);
    }

    @Test
    public void testExecute() {
        task.execute();

        try {
            final List<CountryInfo> result = task.get();
            Collections.sort(COUNTRY_LIST);
            assertEquals(COUNTRY_LIST, result);
        } catch (InterruptedException e) {
            fail("Should not throw InterruptedException");
        } catch (ExecutionException e) {
            fail("Should not throw ExecutionException");
        }
    }

    @Test
    public void testOnPostExecute_nullListener() {
        task = new CountryListLoadTask(null);
        try {
            task.onPostExecute(COUNTRY_LIST);
        } catch (NullPointerException ex) {
            fail("Should not throw NullPointerException");
        }
    }

    @Test
    public void testOnPostExecute() {
        task.onPostExecute(COUNTRY_LIST);
        verify(listener).onLoadComplete(COUNTRY_LIST);
    }
}
