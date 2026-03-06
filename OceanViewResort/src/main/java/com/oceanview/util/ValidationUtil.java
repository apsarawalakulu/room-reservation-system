package com.oceanview.util;

import java.util.regex.Pattern;

public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{10}$");
    
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {
        if (isNullOrEmpty(email)) return false;
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidPhoneNumber(String phone) {
        if (isNullOrEmpty(phone)) return false;
        return PHONE_PATTERN.matcher(phone).matches();
    }
    
    public static boolean isValidName(String name) {
        if (isNullOrEmpty(name)) return false;
        // Allows letters and spaces, 2 to 100 characters.
        return name.matches("^[a-zA-Z\\s]{2,100}$");
    }
}
