package com.mcp.crispy.common.utils;

import java.security.SecureRandom;

public class RandomCodeUtils {
    public static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
    public static final String CHAR_UPPER = CHAR_LOWER.toUpperCase();
    public static final String NUMBER = "0123456789";
    public static final String VERIFICATION_CHARS = CHAR_LOWER + CHAR_UPPER + NUMBER;
    public static final int CODE_LENGTH = 6;
    public static final int PASSWORD_LENGTH = 8;
    public static final SecureRandom random = new SecureRandom();

    public static String generateVerificationCode() {
        return generateRandomString(VERIFICATION_CHARS, CODE_LENGTH);
    }

    public static String generateTempPassword() {
        return generateRandomString(VERIFICATION_CHARS, PASSWORD_LENGTH);
    }

    public static String generateRandomString(String characters, int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }
}
