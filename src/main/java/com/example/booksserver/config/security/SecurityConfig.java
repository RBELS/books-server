package com.example.booksserver.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "/authors**").hasRole(ResourceRole.ADMIN.name())
                        .requestMatchers(HttpMethod.POST, "/books**").hasRole(ResourceRole.ADMIN.name())
                        .anyRequest().permitAll()
                )
                .csrf().disable()
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(
                                        new JwtWithRolesConverterFactory("books-server")
                                                .createConverter()
                                )
                        )
                );
        return http.build();
    }
}
