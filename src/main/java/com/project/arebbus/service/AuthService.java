package com.project.arebbus.service;

import com.project.arebbus.dto.AuthResponse;
import com.project.arebbus.dto.UserLoginRequest;
import com.project.arebbus.dto.UserRegistrationRequest;
import com.project.arebbus.exception.UserAlreadyExists;
import com.project.arebbus.exception.UserNotFound;
import com.project.arebbus.model.User;
import com.project.arebbus.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service class for auth business logic operations.
 * Handles user registration, authentication, and JWT token generation.
 */
@Service
@RequiredArgsConstructor
public class AuthService {
    
    /** Repository for user data access operations */
    /** Repository for  data access */
    private final UserRepository userRepository;

    /** Password encoder for securing user passwords */
    private final PasswordEncoder passwordEncoder;

    /** Spring Security authentication manager */
    private final AuthenticationManager authenticationManager;

    /** Service for JWT token operations */
    /** Service for  operations */
    private final JwtService jwtService;

    /** Logger for this service */
    /** Logger for this service */
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    /**
     * Registers a new user in the system.
     * Validates that the email is not already in use, creates a new user with encoded password,
     * and generates a JWT token for immediate authentication.
     * 
     * @param input The user registration request containing email, name, and password
     * @return AuthResponse containing user details and JWT token
     * @throws UserAlreadyExists if a user with the provided email already exists
     */
    /**
     * Registers a new user in the system.
     * 
     * @param request The registration request with user details
     * @return AuthResponse containing registration status and token
     */
    public AuthResponse register(UserRegistrationRequest input) {
        // Assuming the fields in request body are non-empty
        if (userRepository.existsByEmail(input.getEmail())) {
            throw new UserAlreadyExists(input.getEmail());
        }
        
        // Placeholder image URL for new users
        String fake = "https://picsum.photos/seed/example/300/200";

        // Create new user with encoded password
        User user = User.builder()
                        .email(input.getEmail())
                        .name(input.getName())
                        .password(passwordEncoder.encode(input.getPassword()))
                        .reputation(0)
                        .image(fake)
                        .valid(true)
                        .build();
        userRepository.save(user);

        // Generate JWT token for the newly registered user
        String jwtToken = jwtService.generateToken(user);
        // Cookie handling is commented out for now
        // CookieHelper.addCookie(response,"arebbus", jwtToken);

        return new AuthResponse(user.getId(), "User Registered Successfully", true, jwtToken, user.getName(), user.getImage());
    }

    /**
     * Authenticates an existing user with email and password.
     * Validates the user credentials and generates a JWT token upon successful authentication.
     * 
     * @param input The user login request containing email and password
     * @return AuthResponse containing user details and JWT token
     * @throws UserNotFound if no user exists with the provided email
     */
    /**
     * Authenticates a user with credentials.
     * 
     * @param request The login request with credentials
     * @return AuthResponse containing authentication status and token
     */
    public AuthResponse authenticate(UserLoginRequest input) {
        // Find user by email or throw exception if not found
        User user = userRepository
                .findByEmail(input.getEmail())
                .orElseThrow(() -> new UserNotFound(input.getEmail()));

        // Authenticate user credentials using Spring Security
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );

        logger.info("User successfully authenticated: {}", input.getEmail());
        
        // Generate JWT token for authenticated user
        String jwtToken = jwtService.generateToken(user);
        // Cookie handling is commented out for now
        // CookieHelper.addCookie(response, "arebbus", jwtToken);
        
        return new AuthResponse(user.getId(), "User Successfully Logged in", true, jwtToken, user.getName(), user.getImage());
    }
}