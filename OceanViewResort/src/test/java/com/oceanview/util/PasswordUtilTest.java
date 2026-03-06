package com.oceanview.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordUtilTest {

    @Test
    void testHashPassword() {
        String input = "admin123";
        String hashed = PasswordUtil.hashPassword(input);

        assertNotNull(hashed);
        assertEquals(64, hashed.length()); // SHA-256 hex is 64 chars long

        // Ensure deterministic matching
        String secondHashed = PasswordUtil.hashPassword(input);
        assertEquals(hashed, secondHashed);
    }

    @Test
    void testHashPasswordNull() {
        assertNull(PasswordUtil.hashPassword(null));
    }
}
