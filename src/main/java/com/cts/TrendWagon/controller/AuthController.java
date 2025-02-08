package com.cts.TrendWagon.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/api/auth")
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);		

    @GetMapping("/status")
    @ResponseBody
    public ResponseEntity<?> authStatus(Authentication authentication) {
        logger.info("Received request to check authentication status");

        // Log details from SecurityContextHolder
        Authentication securityContextAuth = SecurityContextHolder.getContext().getAuthentication();
        logger.info("SecurityContextHolder Authentication: {}", securityContextAuth);

        if (authentication == null) {
            logger.warn("Authentication object is null");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }

        logger.info("Authentication object: {}", authentication);
        logger.info("Principal: {}", authentication.getPrincipal());
        logger.info("Authorities: {}", authentication.getAuthorities());
        logger.info("Is authenticated: {}", authentication.isAuthenticated());

        if (authentication.isAuthenticated()) {
            return ResponseEntity.ok("Authenticated");
        } else {
            logger.warn("User is not authenticated");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }
    }
}
