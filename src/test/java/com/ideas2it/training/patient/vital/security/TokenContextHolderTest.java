package com.ideas2it.training.patient.vital.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TokenContextHolderTest {

    @Test
    void testSetAndGetToken() {
        // Arrange
        String token = "testToken";

        // Act
        TokenContextHolder.setToken(token);
        String retrievedToken = TokenContextHolder.getToken();

        // Assert
        assertEquals(token, retrievedToken);
    }

    @Test
    void testSetTokenToNull() {
        // Act
        TokenContextHolder.setToken(null);
        String retrievedToken = TokenContextHolder.getToken();

        // Assert
        assertNull(retrievedToken);
    }

    @Test
    void testClearToken() {
        // Arrange
        TokenContextHolder.setToken("testToken");

        // Act
        TokenContextHolder.clear();
        String retrievedToken = TokenContextHolder.getToken();

        // Assert
        assertNull(retrievedToken);
    }

    @Test
    void testThreadLocalIsolation() throws InterruptedException {
        // Arrange
        TokenContextHolder.setToken("mainThreadToken");

        // Act
        Thread thread = new Thread(() -> {
            TokenContextHolder.setToken("childThreadToken");
            assertEquals("childThreadToken", TokenContextHolder.getToken());
        });
        thread.start();
        thread.join();

        // Assert
        assertEquals("mainThreadToken", TokenContextHolder.getToken());
    }
}
