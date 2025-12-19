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
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**"))

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/uploads/**").permitAll()
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/login", "/register").permitAll() // Add this
                        .requestMatchers("/user/**").hasRole("USER")
                        .requestMatchers("/admin/**").permitAll()
                        .requestMatchers("/api/admin/**").permitAll()
                        .anyRequest().permitAll())

                // Add this section to handle unauthenticated users
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.sendRedirect("/login");
                        }));

        return http.build();
    }
}