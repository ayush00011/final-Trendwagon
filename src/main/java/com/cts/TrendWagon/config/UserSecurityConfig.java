package com.cts.TrendWagon.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@Order(2)
public class UserSecurityConfig {

    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain userSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            // This filter chain applies only to URLs starting with /api/user/
            .securityMatcher("/api/user/**")
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Permit the login/logout and registration endpoints for users
                .requestMatchers("/api/user/login", "/api/user/logout", 
                                  "/api/user/login-success", "/api/user/logout-success",
                                  "/api/user/register")
                .permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginProcessingUrl("/api/user/login")             // URL that processes user login
                .defaultSuccessUrl("/api/user/login-success", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/api/user/logout")
                .logoutSuccessUrl("/api/user/logout-success")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .exceptionHandling(exception ->
                exception.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
            .headers(headers -> headers
                .cacheControl(cache -> cache.disable())
                .addHeaderWriter(new StaticHeadersWriter("Cache-Control", "no-cache, no-store, must-revalidate"))
                .addHeaderWriter(new StaticHeadersWriter("Pragma", "no-cache"))
                .addHeaderWriter(new StaticHeadersWriter("Expires", "0"))
            );
        return http.build();
    }
}
