package com.mnesa.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for OpenAPI 3.0 documentation using SpringDoc.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MNESA Modular Monolith API")
                        .version("0.1.0")
                        .description("REST API for MNESA: AI-powered opportunity capture and follow-through platform.")
                        .contact(new Contact()
                                .name("MNESA Platform Engineering")
                                .email("engineering@mnesa.ai"))
                        .license(new License()
                                .name("Proprietary")
                                .url("https://mnesa.ai/terms")));
    }
}
