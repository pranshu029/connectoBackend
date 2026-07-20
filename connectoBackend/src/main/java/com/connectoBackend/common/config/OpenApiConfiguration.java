package com.connectoBackend.common.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures OpenAPI documentation.
 */
@Configuration
public class OpenApiConfiguration {

    // Configure OpenAPI metadata
    @Bean
    public OpenAPI openAPI() {

        return new OpenAPI()
                .info(
                        new Info()
                                .title("Connecto Backend API")
                                .description("Production-ready messaging backend built with Spring Boot")
                                .version("v1.0")
                                .contact(
                                        new Contact()
                                                .name("Pranshu Dwivedi")
                                                .email("your-email@example.com")
                                )
                                .license(
                                        new License()
                                                .name("MIT License")
                                )
                )
                .externalDocs(
                        new ExternalDocumentation()
                                .description("Project Documentation")
                                .url("https://github.com/your-username/connecto-backend")
                );
    }
}