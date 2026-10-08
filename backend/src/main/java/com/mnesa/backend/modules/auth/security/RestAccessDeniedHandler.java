package com.mnesa.backend.modules.auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public RestAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("type", "https://api.mnesa.com/errors/forbidden");
        body.put("title", "Forbidden");
        body.put("status", HttpServletResponse.SC_FORBIDDEN);
        body.put("detail", accessDeniedException != null && accessDeniedException.getMessage() != null
                ? accessDeniedException.getMessage()
                : "You do not have permission to access this resource");
        body.put("instance", request.getRequestURI());
        body.put("timestamp", Instant.now().toString());

        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
