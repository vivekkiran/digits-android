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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

class PhoneNumberUtils {
    private final static int MAX_COUNTRIES = 291;
    private final SimManager simManager;

    PhoneNumberUtils(SimManager simManager) {
        this.simManager = simManager;
    }

    /**
     * This method should not be called on UI thread. Potentially creates a country code by iso
     * * map which can take long in some devices
     *
     * @return an instance of the PhoneNumber using the SIM information
     */
    protected PhoneNumber getPhoneNumber() {
        if (simManager == null) {
            return PhoneNumber.emptyPhone();
        }
        return createPhoneNumber(createCountryCodeByIsoMap());
    }

    private PhoneNumber createPhoneNumber(Map<String, Integer> countryCodeByIso) {
        final String countryIso = simManager.getCountryIso();
        final String countryCode = getCountryCode(countryIso, countryCodeByIso);
        final String phoneNumber = stripCountryCode(simManager.getRawPhoneNumber(),
                countryCode);

        return new PhoneNumber(phoneNumber, countryIso, countryCode);
    }

    private Map<String, Integer> createCountryCodeByIsoMap() {
        final Map<String, Integer> countryCodeByIso = new HashMap<>(MAX_COUNTRIES);
        countryCodeByIso.put("AF", 93);
        countryCodeByIso.put("AX", 358);
        countryCodeByIso.put("AL", 355);
        countryCodeByIso.put("DZ", 213);
        countryCodeByIso.put("AS", 1);
        countryCodeByIso.put("AD", 376);
        countryCodeByIso.put("AO", 244);
        countryCodeByIso.put("AI", 1);
        countryCodeByIso.put("AG", 1);
        countryCodeByIso.put("AR", 54);
        countryCodeByIso.put("AM", 374);
        countryCodeByIso.put("AW", 297);
        countryCodeByIso.put("AC", 247);
        countryCodeByIso.put("AU", 61);
        countryCodeByIso.put("AT", 43);
        countryCodeByIso.put("AZ", 994);
        countryCodeByIso.put("BS", 1);
        countryCodeByIso.put("BH", 973);
        countryCodeByIso.put("BD", 880);
        countryCodeByIso.put("BB", 1);
        countryCodeByIso.put("BY", 375);
        countryCodeByIso.put("BE", 32);
        countryCodeByIso.put("BZ", 501);
        countryCodeByIso.put("BJ", 229);
        countryCodeByIso.put("BM", 1);
        countryCodeByIso.put("BT", 975);
        countryCodeByIso.put("BO", 591);
        countryCodeByIso.put("BA", 387);
        countryCodeByIso.put("BW", 267);
        countryCodeByIso.put("BR", 55);
        countryCodeByIso.put("IO", 246);
        countryCodeByIso.put("VG", 1);
        countryCodeByIso.put("BN", 673);
        countryCodeByIso.put("BG", 359);
        countryCodeByIso.put("BF", 226);
        countryCodeByIso.put("BI", 257);
        countryCodeByIso.put("KH", 855);
        countryCodeByIso.put("CM", 237);
        countryCodeByIso.put("CA", 1);
        countryCodeByIso.put("CV", 238);
        countryCodeByIso.put("BQ", 599);
        countryCodeByIso.put("KY", 1);
        countryCodeByIso.put("CF", 236);
        countryCodeByIso.put("TD", 235);
        countryCodeByIso.put("CL", 56);
        countryCodeByIso.put("CN", 86);
        countryCodeByIso.put("CX", 61);
        countryCodeByIso.put("CC", 61);
        countryCodeByIso.put("CO", 57);
        countryCodeByIso.put("KM", 269);
        countryCodeByIso.put("CD", 243);
        countryCodeByIso.put("CG", 242);
        countryCodeByIso.put("CK", 682);
        countryCodeByIso.put("CR", 506);
        countryCodeByIso.put("CI", 225);
        countryCodeByIso.put("HR", 385);
        countryCodeByIso.put("CU", 53);
        countryCodeByIso.put("CW", 599);
        countryCodeByIso.put("CY", 357);
        countryCodeByIso.put("CZ", 420);
        countryCodeByIso.put("DK", 45);
        countryCodeByIso.put("DJ", 253);
        countryCodeByIso.put("DM", 1);
        countryCodeByIso.put("DO", 1);
        countryCodeByIso.put("TL", 670);
        countryCodeByIso.put("EC", 593);
        countryCodeByIso.put("EG", 20);
        countryCodeByIso.put("SV", 503);
        countryCodeByIso.put("GQ", 240);
        countryCodeByIso.put("ER", 291);
        countryCodeByIso.put("EE", 372);
        countryCodeByIso.put("ET", 251);
        countryCodeByIso.put("FK", 500);
        countryCodeByIso.put("FO", 298);
        countryCodeByIso.put("FJ", 679);
        countryCodeByIso.put("FI", 358);
        countryCodeByIso.put("FR", 33);
        countryCodeByIso.put("GF", 594);
        countryCodeByIso.put("PF", 689);
        countryCodeByIso.put("GA", 241);
        countryCodeByIso.put("GM", 220);
        countryCodeByIso.put("GE", 995);
        countryCodeByIso.put("DE", 49);
        countryCodeByIso.put("GH", 233);
        countryCodeByIso.put("GI", 350);
        countryCodeByIso.put("GR", 30);
        countryCodeByIso.put("GL", 299);
        countryCodeByIso.put("GD", 1);
        countryCodeByIso.put("GP", 590);
        countryCodeByIso.put("GU", 1);
        countryCodeByIso.put("GT", 502);
        countryCodeByIso.put("GG", 44);
        countryCodeByIso.put("GN", 224);
        countryCodeByIso.put("GW", 245);
        countryCodeByIso.put("GY", 592);
        countryCodeByIso.put("HT", 509);
        countryCodeByIso.put("HM", 672);
        countryCodeByIso.put("HN", 504);
        countryCodeByIso.put("HK", 852);
        countryCodeByIso.put("HU", 36);
        countryCodeByIso.put("IS", 354);
        countryCodeByIso.put("IN", 91);
        countryCodeByIso.put("ID", 62);
        countryCodeByIso.put("IR", 98);
        countryCodeByIso.put("IQ", 964);
        countryCodeByIso.put("IE", 353);
        countryCodeByIso.put("IM", 44);
        countryCodeByIso.put("IL", 972);
        countryCodeByIso.put("IT", 39);
        countryCodeByIso.put("JM", 1);
        countryCodeByIso.put("JP", 81);
        countryCodeByIso.put("JE", 44);
        countryCodeByIso.put("JO", 962);
        countryCodeByIso.put("KZ", 7);
        countryCodeByIso.put("KE", 254);
        countryCodeByIso.put("KI", 686);
        countryCodeByIso.put("XK", 381);
        countryCodeByIso.put("KW", 965);
        countryCodeByIso.put("KG", 996);
        countryCodeByIso.put("LA", 856);
        countryCodeByIso.put("LV", 371);
        countryCodeByIso.put("LB", 961);
        countryCodeByIso.put("LS", 266);
        countryCodeByIso.put("LR", 231);
        countryCodeByIso.put("LY", 218);
        countryCodeByIso.put("LI", 423);
        countryCodeByIso.put("LT", 370);
        countryCodeByIso.put("LU", 352);
        countryCodeByIso.put("MO", 853);
        countryCodeByIso.put("MK", 389);
        countryCodeByIso.put("MG", 261);
        countryCodeByIso.put("MW", 265);
        countryCodeByIso.put("MY", 60);
        countryCodeByIso.put("MV", 960);
        countryCodeByIso.put("ML", 223);
        countryCodeByIso.put("MT", 356);
        countryCodeByIso.put("MH", 692);
        countryCodeByIso.put("MQ", 596);
        countryCodeByIso.put("MR", 222);
        countryCodeByIso.put("MU", 230);
        countryCodeByIso.put("YT", 262);
        countryCodeByIso.put("MX", 52);
        countryCodeByIso.put("FM", 691);
        countryCodeByIso.put("MD", 373);
        countryCodeByIso.put("MC", 377);
        countryCodeByIso.put("MN", 976);
        countryCodeByIso.put("ME", 382);
        countryCodeByIso.put("MS", 1);
        countryCodeByIso.put("MA", 212);
        countryCodeByIso.put("MZ", 258);
        countryCodeByIso.put("MM", 95);
        countryCodeByIso.put("NA", 264);
        countryCodeByIso.put("NR", 674);
        countryCodeByIso.put("NP", 977);
        countryCodeByIso.put("NL", 31);
        countryCodeByIso.put("NC", 687);
        countryCodeByIso.put("NZ", 64);
        countryCodeByIso.put("NI", 505);
        countryCodeByIso.put("NE", 227);
        countryCodeByIso.put("NG", 234);
        countryCodeByIso.put("NU", 683);
        countryCodeByIso.put("NF", 672);
        countryCodeByIso.put("KP", 850);
        countryCodeByIso.put("MP", 1);
        countryCodeByIso.put("NO", 47);
        countryCodeByIso.put("OM", 968);
        countryCodeByIso.put("PK", 92);
        countryCodeByIso.put("PW", 680);
        countryCodeByIso.put("PS", 970);
        countryCodeByIso.put("PA", 507);
        countryCodeByIso.put("PG", 675);
        countryCodeByIso.put("PY", 595);
        countryCodeByIso.put("PE", 51);
        countryCodeByIso.put("PH", 63);
        countryCodeByIso.put("PL", 48);
        countryCodeByIso.put("PT", 351);
        countryCodeByIso.put("PR", 1);
        countryCodeByIso.put("QA", 974);
        countryCodeByIso.put("RE", 262);
        countryCodeByIso.put("RO", 40);
        countryCodeByIso.put("RU", 7);
        countryCodeByIso.put("RW", 250);
        countryCodeByIso.put("BL", 590);
        countryCodeByIso.put("SH", 290);
        countryCodeByIso.put("KN", 1);
        countryCodeByIso.put("LC", 1);
        countryCodeByIso.put("MF", 590);
        countryCodeByIso.put("PM", 508);
        countryCodeByIso.put("VC", 1);
        countryCodeByIso.put("WS", 685);
        countryCodeByIso.put("SM", 378);
        countryCodeByIso.put("ST", 239);
        countryCodeByIso.put("SA", 966);
        countryCodeByIso.put("SN", 221);
        countryCodeByIso.put("RS", 381);
        countryCodeByIso.put("SC", 248);
        countryCodeByIso.put("SL", 232);
        countryCodeByIso.put("SG", 65);
        countryCodeByIso.put("SX", 1);
        countryCodeByIso.put("SK", 421);
        countryCodeByIso.put("SI", 386);
        countryCodeByIso.put("SB", 677);
        countryCodeByIso.put("SO", 252);
        countryCodeByIso.put("ZA", 27);
        countryCodeByIso.put("GS", 500);
        countryCodeByIso.put("KR", 82);
        countryCodeByIso.put("SS", 211);
        countryCodeByIso.put("ES", 34);
        countryCodeByIso.put("LK", 94);
        countryCodeByIso.put("SD", 249);
        countryCodeByIso.put("SR", 597);
        countryCodeByIso.put("SJ", 47);
        countryCodeByIso.put("SZ", 268);
        countryCodeByIso.put("SE", 46);
        countryCodeByIso.put("CH", 41);
        countryCodeByIso.put("SY", 963);
        countryCodeByIso.put("TW", 886);
        countryCodeByIso.put("TJ", 992);
        countryCodeByIso.put("TZ", 255);
        countryCodeByIso.put("TH", 66);
        countryCodeByIso.put("TG", 228);
        countryCodeByIso.put("TK", 690);
        countryCodeByIso.put("TO", 676);
        countryCodeByIso.put("TT", 1);
        countryCodeByIso.put("TN", 216);
        countryCodeByIso.put("TR", 90);
        countryCodeByIso.put("TM", 993);
        countryCodeByIso.put("TC", 1);
        countryCodeByIso.put("TV", 688);
        countryCodeByIso.put("VI", 1);
        countryCodeByIso.put("UG", 256);
        countryCodeByIso.put("UA", 380);
        countryCodeByIso.put("AE", 971);
        countryCodeByIso.put("GB", 44);
        countryCodeByIso.put("US", 1);
        countryCodeByIso.put("UY", 598);
        countryCodeByIso.put("UZ", 998);
        countryCodeByIso.put("VU", 678);
        countryCodeByIso.put("VA", 379);
        countryCodeByIso.put("VE", 58);
        countryCodeByIso.put("VN", 84);
        countryCodeByIso.put("WF", 681);
        countryCodeByIso.put("EH", 212);
        countryCodeByIso.put("YE", 967);
        countryCodeByIso.put("ZM", 260);
        countryCodeByIso.put("ZW", 263);
        return countryCodeByIso;
    }

    private String getCountryCode(String countryIso, Map<String, Integer> result) {
        if (countryIso == null) {
            return "";
        }
        final Integer countryCode = result.get(countryIso.toUpperCase(Locale.getDefault()));
        return countryCode == null ? "" : countryCode.toString();
    }

    private String stripCountryCode(String phoneNumber, String countryCode) {
        return phoneNumber.replaceFirst("^\\+?" + countryCode, "");
    }
}
