package com.connectoBackend.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        // -> Frontend URL
        configuration.setAllowedOrigins(
                List.of("http://localhost:5175")
        );

        // -> Allowed HTTP methods
        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "PATCH",
                        "DELETE",
                        "OPTIONS"
                )
        );

        // -> Allow all request headers
        configuration.setAllowedHeaders(List.of("*"));

        // -> Allow cookies/authentication credentials
        configuration.setAllowCredentials(true);

        // -> Expose Authorization header if needed by frontend
        configuration.setExposedHeaders(
                List.of("Authorization")
        );

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        // -> Apply CORS configuration to all API endpoints
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}