package com.cts.TrendWagon.controller;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cts.TrendWagon.dto.AdminLoginResponse;
import com.cts.TrendWagon.dto.ChangePassword;
import com.cts.TrendWagon.model.User;
import com.cts.TrendWagon.repository.UserRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


@Controller
@RequestMapping("/api/admin")
public class AdminController {
	private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AuthenticationManager authenticationManager;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


 // Login User
    @GetMapping(value= "/login",produces = "application/json")
    @ResponseBody
    public ResponseEntity<AdminLoginResponse> loginUser(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            HttpServletRequest request,
            HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
            );
            
            boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

            if (!isAdmin) {
                return ResponseEntity.status(403).body(new AdminLoginResponse("Access Denied: Only admins can log in", null));
            }
            String adminName = "Unknown Admin";
            if (authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User) {
                org.springframework.security.core.userdetails.User userDetails =
                        (org.springframework.security.core.userdetails.User) authentication.getPrincipal();

                // Fetch user details from the database using email
                Optional<User> user = userRepository.findByEmail(userDetails.getUsername());
                if (user.isPresent()) {
                    adminName = user.get().getName();
                }
            }

            SecurityContextHolder.getContext().setAuthentication(authentication);

            HttpSession session = request.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

            // Explicitly set session cookie
            Cookie sessionCookie = new Cookie("JSESSIONID", session.getId());
            sessionCookie.setPath("/");
            sessionCookie.setHttpOnly(true);
            sessionCookie.setMaxAge(60 * 60); // 1 hour
            response.addCookie(sessionCookie);

			/*
			 * logger.info("Session ID after login: {}", session.getId());
			 * logger.info("Authentication after login: {}",
			 * SecurityContextHolder.getContext().getAuthentication());
			 */
			/* logger.info(adminName); */
			
			  AdminLoginResponse responseBody = new AdminLoginResponse("Admin Login Successful", adminName);
			
				 return ResponseEntity.ok(responseBody);
          
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body(new AdminLoginResponse("Invalid credentials", null));
        }
    } 
    
    // Login success response
    @GetMapping("/login-success")
    public ResponseEntity<String> loginSuccess() {
        return ResponseEntity.ok("Login successful!");
    }
    

    // Logout success response
    @GetMapping("/logout-success")
    public ResponseEntity<String> logoutSuccess() {
        return ResponseEntity.ok("Logged out successfully!");
    }
    // Change Password using session-based authentication
    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePassword passwordRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // Get logged-in user email

        Optional<User> dbUser = userRepository.findByEmail(email);
        if (dbUser.isPresent() && passwordEncoder.matches(passwordRequest.getOldPassword(), dbUser.get().getPassword())) {
            dbUser.get().setPassword(passwordEncoder.encode(passwordRequest.getNewPassword()));
            userRepository.save(dbUser.get());
            return ResponseEntity.ok("Password changed successfully!");
        }
        return ResponseEntity.status(401).body("Invalid old password");
    }
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }

        // Invalidate session
        request.getSession().invalidate();

        // Remove session cookie
        removeSessionCookie(response);
        
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");

        return ResponseEntity.ok("Logged out successfully!");
    }

    // Helper method to remove session cookie
    private void removeSessionCookie(HttpServletResponse response) {
        javax.servlet.http.Cookie cookie = new javax.servlet.http.Cookie("JSESSIONID", null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // Ensure this is true if running over HTTPS
        cookie.setMaxAge(0); // Expire cookie immediately
        response.addCookie(cookie);
    }
}
