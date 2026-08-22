package com.connectoBackend.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

        @Value("${application.cors.allowed-origins}")
        private List<String> allowedOrigins;

//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//
//        CorsConfiguration configuration = new CorsConfiguration();
//
//        configuration.setAllowedOrigins(allowedOrigins);
//
//        // -> Allowed HTTP methods
//        configuration.setAllowedMethods(
//                List.of(
//                        "GET",
//                        "POST",
//                        "PUT",
//                        "PATCH",
//                        "DELETE",
//                        "OPTIONS"
//                )
//        );
//
//        // -> Allow all request headers
//        configuration.setAllowedHeaders(List.of("*"));
//
//        // -> Allow cookies/authentication credentials
//        configuration.setAllowCredentials(true);
//
//        // -> Expose Authorization header if needed by frontend
//        configuration.setExposedHeaders(
//                List.of("Authorization")
//        );
//
//        UrlBasedCorsConfigurationSource source =
//                new UrlBasedCorsConfigurationSource();
//
//        // -> Apply CORS configuration to all API endpoints
//        source.registerCorsConfiguration("/**", configuration);
//
//        return source;
//    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        // -> Allow Vercel frontend deployments
        configuration.setAllowedOriginPatterns(
                List.of("https://*.vercel.app")
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

        // -> Expose Authorization header
        configuration.setExposedHeaders(
                List.of("Authorization")
        );

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}