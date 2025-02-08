package com.cts.TrendWagon.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@Order(4)
public class ProductSecurityConfig {

    private final CorsConfigurationSource corsConfigurationSource;

    public ProductSecurityConfig(CorsConfigurationSource corsConfigurationSource) {
        this.corsConfigurationSource = corsConfigurationSource;
    }

    @Bean
    public SecurityFilterChain productSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/api/products/**") // Apply security only to product endpoints
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                
            		// Public: Serve product images
                
            		.requestMatchers("/api/products/images/**","/api/products/user-view").permitAll()
                .requestMatchers("/api/products").hasRole("ADMIN")
                .requestMatchers("/api/products/{id}").hasRole("ADMIN")
                .requestMatchers("/api/products/**").hasRole("ADMIN")
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));

        return http.build();
    }
}
