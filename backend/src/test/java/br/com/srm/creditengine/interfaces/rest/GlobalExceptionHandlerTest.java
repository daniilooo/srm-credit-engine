package br.com.srm.creditengine.interfaces.rest;

import br.com.srm.creditengine.interfaces.rest.dto.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    private MockHttpServletRequest requestFor(String uri) {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setRequestURI(uri);
        return req;
    }

    @Test
    void handleConstraintViolation_returns400_withMessage() {
        ConstraintViolationException ex = new ConstraintViolationException(
                "base: must not be null", Set.of());
        MockHttpServletRequest req = requestFor("/api/v1/exchange-rates/latest");

        ResponseEntity<ErrorResponse> response = handler.handleConstraintViolation(ex, req);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Bad Request", response.getBody().error());
        assertEquals("base: must not be null", response.getBody().message());
        assertEquals("/api/v1/exchange-rates/latest", response.getBody().path());
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    void handleGeneric_returns500_withGenericMessage() {
        Exception ex = new RuntimeException("unexpected internal failure");
        MockHttpServletRequest req = requestFor("/api/v1/settlements");

        ResponseEntity<ErrorResponse> response = handler.handleGeneric(ex, req);

        assertEquals(500, response.getStatusCode().value());
        assertEquals("Internal Server Error", response.getBody().error());
        assertEquals("An unexpected error occurred", response.getBody().message());
    }

    @Test
    @SuppressWarnings("unchecked")
    void handleTypeMismatch_returns400_withClearMessage() {
        MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);
        when(ex.getValue()).thenReturn("INVALID");
        when(ex.getName()).thenReturn("base");
        MockHttpServletRequest req = requestFor("/api/v1/exchange-rates/latest");

        ResponseEntity<ErrorResponse> response = handler.handleTypeMismatch(ex, req);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Invalid value 'INVALID' for parameter 'base'", response.getBody().message());
    }
}
