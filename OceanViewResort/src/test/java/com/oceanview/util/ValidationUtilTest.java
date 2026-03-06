package com.oceanview.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidationUtilTest {

    @Test
    void testValidNames() {
        assertTrue(ValidationUtil.isValidName("John Doe"));
        assertTrue(ValidationUtil.isValidName("Jane"));
        assertFalse(ValidationUtil.isValidName("J")); // Too short
        assertFalse(ValidationUtil.isValidName("John 123")); // Contains numbers
        assertFalse(ValidationUtil.isValidName("")); // Empty
        assertFalse(ValidationUtil.isValidName(null)); // Null
    }

    @Test
    void testValidEmail() {
        assertTrue(ValidationUtil.isValidEmail("guest@test.com"));
        assertTrue(ValidationUtil.isValidEmail("john.doe@test.co.uk"));
        assertFalse(ValidationUtil.isValidEmail("guesttest.com"));
        assertFalse(ValidationUtil.isValidEmail("guest@"));
        assertFalse(ValidationUtil.isValidEmail(""));
        assertFalse(ValidationUtil.isValidEmail(null));
    }

    @Test
    void testValidPhoneNumber() {
        assertTrue(ValidationUtil.isValidPhoneNumber("0712345678"));
        assertTrue(ValidationUtil.isValidPhoneNumber("1234567890"));
        assertFalse(ValidationUtil.isValidPhoneNumber("0712345")); // Too short
        assertFalse(ValidationUtil.isValidPhoneNumber("07123456789")); // Too long
        assertFalse(ValidationUtil.isValidPhoneNumber("ABCDEFGHIJ")); // Letters
        assertFalse(ValidationUtil.isValidPhoneNumber(""));
        assertFalse(ValidationUtil.isValidPhoneNumber(null));
    }
}
