package com.example.app.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleRuntimeException_returns400ForGenericError() {
        RuntimeException ex = new RuntimeException("Username is already taken");

        ResponseEntity<ErrorResponse> response = handler.handleRuntimeException(ex);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Username is already taken", response.getBody().getMessage());
    }

    @Test
    void handleRuntimeException_returns401ForUnauthorized() {
        RuntimeException ex = new RuntimeException("Unauthorized");

        ResponseEntity<ErrorResponse> response = handler.handleRuntimeException(ex);

        assertEquals(401, response.getStatusCode().value());
        assertEquals("Unauthorized", response.getBody().getMessage());
    }

    @Test
    void handleException_returns500ForGenericException() {
        Exception ex = new Exception("Something went wrong internally");

        ResponseEntity<ErrorResponse> response = handler.handleException(ex);

        assertEquals(500, response.getStatusCode().value());
        assertEquals("Internal server error", response.getBody().getMessage());
    }
}
