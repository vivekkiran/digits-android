# Digits Android SDK changelog
*Non-trivial pull requests should include an entry below. Entries must be suitable for inclusion in public-facing materials such as release notes and blog posts. Keep them short, sweet, and in the past tense. New entries go on top. When merging to deploy, add the version number and date.*

## Unreleased

## v1.9.1

* Updated translations.

## v1.9.0
* Added Email feature
* Fixed parsing of confirmation code from SMS.
* Removed usage of deprecated Apache HTTP Client constants.

## v1.8.0
* Added dgts__logoDrawable to allow providing a custom logo
* Added voice call verification to the auth flow
* Raised Min SDK version from 8 to 9.

## v1.7.2

## v1.7.1

* (IC) Fixed issue that invalidates current session.

## v1.7.0

* (IC) Added SessionListener to receive session changes.

## v1.6.2

* (EF) Use AuthRequestQueue for all API request to ensure we always have a valid guest auth token
* (EF) Fixed critical issue where Digits sessions are lost when using Proguard.

## v1.6.1
* (IC) Add weak reference to hold the AuthCallback from the developer in LoginResultReceiver

## v1.6.0
* (IC) [Fixed crash on StateButton when multiple clicks] (https://github
.com/twitter/digits-android/issues/2)
* (IC) Added a new public API to launch authentication flow with the provided phone number

## v1.5.0

* Prepared for open source release.

## v1.4.2

* (IC) Added normalized phone number from /device/login endpoint
* (EF) Change xauth_phone_number.json endpoint to sdk/login endpoint
* (EF) Change over to api.digits.com

## v1.4.1
* (IC) Added normalized phone number from /device/register endpoint
* (IC) Changed host to DigitsApi for ContactsClient

## v1.4.0
* (EF) Set selected country on country list spinner using info from SIM
* (EF) Removed "Number associated with Twitter" fallback strings.

## v1.3.0
* (IC) Fixed UI bug on StateButton
* (EF) Added hasUserGrantedPermission to Contacts API.
* (IC) Read Phone number from sim device
* (EF) Read SMS code from device if RECEIVE_SMS permission is present.

## v1.2.0
**Jan 29 2015**

* (AP) Use SessionMonitor to automatically call verify_credentials with active user sessions
* (EF) Removed targetSdkVersion because it should not be specified on libraries.
* (EF) Removed Twitter Login from fallback.
* (IC) Fixed Resources$NotFoundException on Gingerbread devices.
* (EF) Fixed theme detection when building with Eclipse.
* (IC) Fixed stale guest auth token issue.
* (EF) Added "Find Your Friends" feature.
* (IC) Added Pin code request with theming support.

## v1.1.0
**Dec 15 2014**

* (EF) Added theme support.

## v1.0.2
**Nov 20 2014**
* (TS) Moved to Java 7.

## v1.0.1
**Oct 30 2014**

* (EF) Updated fallback strings for Twitter numbers.
* (TY) Removed Apache 2.0 License from pom files.

## v1.0.0
**Oct 15 2014**

* (LTM) Removed allowBackup=true attribute from application element in AndroidManifest.xml.
* (IC) Fixed bug for correct Digits flow API<16.
* (YH) DigitsClient member of Digits is initialized on the background or on demand.
* (LTM) Updated Digits to provide preference keys to PersistedSessionManager.
* (IC) Added parameter for custom digits sms.
* (EF) Added phone number to authCallback.
* (IC) Replaced internal resources prefix tw by dgts.
* (IC) Added Localization to country code spinner.
* (LTM) Refactor common styles, colors, dimens from Digits to Twitter.
* (IC) Added normalization to phone number for all the requests.
* (EF) Refactor Digits package name to com.digits.sdk.android.
* (EF) Added initial Address Book feature.
* (LTM) Updated Twitter API User model to be immutable model.
* (IC) Fixed bug that allows digits to work with non-US phone numbers.
* (IC) Added Twitter Login fallback.

* Initial version
