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
        RuntimeException ex = new RuntimeException("At least one metric must be selected");

        ResponseEntity<ErrorResponse> response = handler.handleRuntimeException(ex);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("At least one metric must be selected", response.getBody().getMessage());
    }

    @Test
    void handleRuntimeException_returns401ForUnauthorized() {
        RuntimeException ex = new RuntimeException("Unauthorized");

        ResponseEntity<ErrorResponse> response = handler.handleRuntimeException(ex);

        assertEquals(401, response.getStatusCode().value());
    }

    @Test
    void handleException_returns500() {
        Exception ex = new Exception("Unexpected failure");

        ResponseEntity<ErrorResponse> response = handler.handleException(ex);

        assertEquals(500, response.getStatusCode().value());
        assertEquals("Internal server error", response.getBody().getMessage());
    }
}
