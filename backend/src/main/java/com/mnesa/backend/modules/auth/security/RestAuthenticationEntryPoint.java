package com.mnesa.backend.modules.auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public RestAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("type", "https://api.mnesa.com/errors/unauthorized");
        body.put("title", "Unauthorized");
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("detail", authException != null && authException.getMessage() != null
                ? authException.getMessage()
                : "Full authentication is required to access this resource");
        body.put("instance", request.getRequestURI());
        body.put("timestamp", Instant.now().toString());

        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
