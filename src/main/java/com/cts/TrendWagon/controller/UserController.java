package com.cts.TrendWagon.controller;
import java.time.LocalDate;
import java.util.Optional;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cts.TrendWagon.dto.UserCreate;
import com.cts.TrendWagon.dto.UserLoginResponse;
import com.cts.TrendWagon.model.User;
import com.cts.TrendWagon.repository.UserRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


@Controller
@RequestMapping("/api/user")
public class UserController {
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);


    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AuthenticationManager authenticationManager;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Create User with Hashed Password
   @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserCreate userInput) {
        User user = new User();
        user.setName(userInput.getName());
        user.setEmail(userInput.getEmail());
        user.setPassword(passwordEncoder.encode(userInput.getPassword()));
        user.setJoiningDate(LocalDate.now()); // Default joining date
        user.setRole("USER"); // Default role
        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully!");
    }

   @GetMapping(value = "/login", produces = "application/json")
   @ResponseBody
   public ResponseEntity<UserLoginResponse> loginUser(
           @RequestParam("email") String email,
           @RequestParam("password") String password,
           HttpServletRequest request,
           HttpServletResponse response) {
       try {
           Authentication authentication = authenticationManager.authenticate(
                   new UsernamePasswordAuthenticationToken(email, password)
           );

           boolean isUser = authentication.getAuthorities().stream()
                   .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_USER"));

           if (!isUser) {
               return ResponseEntity.status(403).body(new UserLoginResponse("Access Denied: Only users can log in", null, null));
           }

           Optional<User> user = userRepository.findByEmail(email);
           if (user.isEmpty()) {
               return ResponseEntity.status(401).body(new UserLoginResponse("Invalid credentials", null, null));
           }

           User loggedInUser = user.get();
           String userName = loggedInUser.getName();
           LocalDate joiningDate = loggedInUser.getJoiningDate();

           SecurityContextHolder.getContext().setAuthentication(authentication);

           HttpSession session = request.getSession(true);
           session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

           Cookie sessionCookie = new Cookie("JSESSIONID", session.getId());
           sessionCookie.setPath("/");
           sessionCookie.setHttpOnly(true);
           sessionCookie.setMaxAge(60 * 60); // 1 hour
           response.addCookie(sessionCookie);

           UserLoginResponse responseBody = new UserLoginResponse("User Login Successful", userName, joiningDate);

           return ResponseEntity.ok(responseBody);
       } catch (AuthenticationException e) {
           return ResponseEntity.status(401).body(new UserLoginResponse("Invalid credentials", null, null));
       }
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


}
