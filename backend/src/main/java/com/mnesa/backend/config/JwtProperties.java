package com.mnesa.backend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "mnesa.security.jwt")
@Getter
@Setter
public class JwtProperties {
    private String secretKey = "mnesa_dev_jwt_secret_key_minimum_256_bits_length_for_hmac_sha256_secure_2026!";
    private long accessTokenExpirationMs = 3600000; // 1 hour
    private long refreshTokenExpirationMs = 2592000000L; // 30 days
}
