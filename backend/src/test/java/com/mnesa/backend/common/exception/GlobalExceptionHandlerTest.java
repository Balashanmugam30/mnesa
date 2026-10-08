package com.mnesa.backend.common.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("MnesaException transforms to RFC 7807 ProblemDetail with custom properties")
    void shouldTransformMnesaExceptionToProblemDetail() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/opportunities/test-id");

        ResourceNotFoundException ex = new ResourceNotFoundException("Opportunity", "test-id");
        ResponseEntity<ProblemDetail> response = handler.handleMnesaException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        ProblemDetail body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getStatus()).isEqualTo(404);
        assertThat(body.getDetail()).contains("Opportunity with identifier 'test-id' was not found");
        assertThat(body.getInstance().toString()).isEqualTo("/api/v1/opportunities/test-id");
        assertThat(body.getProperties()).containsKey("code");
        assertThat(body.getProperties().get("code")).isEqualTo("RESOURCE_NOT_FOUND");
    }

    @Test
    @DisplayName("General Exception transforms to 500 Internal Server Error ProblemDetail")
    void shouldTransformGeneralExceptionToProblemDetail() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/unknown");

        RuntimeException ex = new RuntimeException("Unexpected database failure");
        ResponseEntity<ProblemDetail> response = handler.handleGeneralException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        ProblemDetail body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getStatus()).isEqualTo(500);
        assertThat(body.getProperties().get("code")).isEqualTo("INTERNAL_SERVER_ERROR");
    }
}
