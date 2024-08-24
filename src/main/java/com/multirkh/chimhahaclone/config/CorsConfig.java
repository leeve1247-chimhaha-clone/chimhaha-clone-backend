package com.multirkh.chimhahaclone.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Collections;
import java.util.List;

@Configuration
public class CorsConfig {
    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.cors(corsConfigurer -> corsConfigurer.configurationSource(_request -> {
            CorsConfiguration cors = new CorsConfiguration();
            cors.setAllowedOrigins(List.of("http://localhost:5173"));
            cors.setAllowedMethods(List.of("*"));
            cors.setAllowCredentials(true);
            cors.setAllowedHeaders(Collections.singletonList("*"));
            return cors;
        }));
        return http.build();
    }
}
