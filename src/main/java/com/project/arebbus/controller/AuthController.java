package com.project.arebbus.controller;

import com.project.arebbus.dto.AuthResponse;
import com.project.arebbus.dto.UserLoginRequest;
import com.project.arebbus.dto.UserRegistrationRequest;
import com.project.arebbus.service.AuthService;
import com.project.arebbus.service.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for handling authentication-related operations.
 * Provides endpoints for user registration, login, and authentication testing.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    
    /** Service for JWT token operations */
    private final JwtService jwtService;
    
    /** Service for authentication business logic */
    private final AuthService authService;

    /** Logger for this controller */
    /** Logger for this controller */
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    /**
     * Test endpoint to verify the authentication controller is working.
     * 
     * @return A simple test message
     */
    @GetMapping("/test")
    public String getMethodName() {
        return "In auth";
    }

    /**
     * Registers a new user in the system.
     * 
     * @param request The user registration request containing user details
     * @param response1 HTTP servlet response for potential cookie handling
     * @return ResponseEntity containing the authentication response
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody UserRegistrationRequest request, HttpServletResponse response1) {
        AuthResponse response = authService.register(request);
        // Cookie handling is commented out for now
        // if (response.isSuccess()) {
        //     CookieHelper.addCookie(response1, "arebbus", response.getToken());
        //     response.setToken(null);
        // }
        return ResponseEntity.ok(response);
    }

    /**
     * Authenticates an existing user and provides login functionality.
     * 
     * @param request The user login request containing credentials
     * @param response1 HTTP servlet response for potential cookie handling
     * @return ResponseEntity containing the authentication response
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody UserLoginRequest request, HttpServletResponse response1) {
        AuthResponse response = authService.authenticate(request);
        // Cookie handling is commented out for now
        // if (response.isSuccess()) {
        //     CookieHelper.addCookie(response1, "arebbus", response.getToken());
        //     response.setToken(null);
        // }
        return ResponseEntity.ok(response);
    }
}
