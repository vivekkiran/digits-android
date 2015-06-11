package com.digits.sdk.android;

public class TestConstants {
    public static final String TOKEN = "token";
    public static final String SECRET = "secret";
    public static final long USER_ID = 11;

    public static DigitsSessionResponse LOGGED_OUT_USER = getDigitsSessionResponse(
            TestConstants.TOKEN, TestConstants.SECRET, DigitsSession.LOGGED_OUT_USER_ID);

    public static DigitsSessionResponse DIGITS_USER = getDigitsSessionResponse(
        TestConstants.TOKEN, TestConstants.SECRET, TestConstants.USER_ID);

    private static DigitsSessionResponse getDigitsSessionResponse(String token, String secret,
            long userId) {
        final DigitsSessionResponse response = new DigitsSessionResponse();
        response.token = token;
        response.secret = secret;
        response.userId = userId;
        return response;
    }
}
