package com.example.clebackend.util;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class EmailValidator {
    private static final String EMAIL_PATTERN = "^(\\w+([-.][A-Za-z0-9]+)*){3,18}@\\w+([-.][A-Za-z0-9]+)*\\.\\w+([-.][A-Za-z0-9]+)*$";

    public static boolean validate(String email) {
        if ((email != null) && (!email.isEmpty())) {
            return Pattern.matches(EMAIL_PATTERN, email);
        }
        return false;
    }
}
