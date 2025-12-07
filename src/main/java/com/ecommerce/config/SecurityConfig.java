package com.ecommerce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF Protection - Enabled for forms, disabled for API
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**"))

                // Authorization rules
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/uploads/**").permitAll()
                        .requestMatchers("/").permitAll()

                        // Allow all admin paths - your interceptor handles auth
                        .requestMatchers("/admin/**").permitAll()
                        .requestMatchers("/api/admin/**").permitAll()

                        // All other requests allowed
                        .anyRequest().permitAll());

        return http.build();
    }
}