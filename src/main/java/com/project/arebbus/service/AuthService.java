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

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    public AuthResponse register(UserRegistrationRequest input) {
        /*
        * Assuming the fields in request body is non-empty
        * */
        if (userRepository.existsByEmail(input.getEmail())) {
            throw new UserAlreadyExists(input.getEmail());
        }
        String fake = "https://picsum.photos/seed/example/300/200";

        User user = User.builder()
                        .email(input.getEmail())
                        .name(input.getName())
                        .password(passwordEncoder.encode(input.getPassword()))
                        .reputation(0)
                        .image(fake)
                        .valid(true)
                        .build();
        userRepository.save(user);

        String jwtToken = jwtService.generateToken(user);
//        CookieHelper.addCookie(response,"arebbus", jwtToken);

        return new AuthResponse(user.getId(), "User Registered Successfully", true, jwtToken);
    }

    public AuthResponse authenticate(UserLoginRequest input) {
        User user = userRepository
                .findByEmail(input.getEmail())
                .orElseThrow(() -> new UserNotFound(input.getEmail()));

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );

        logger.info("Authenticated");
        String jwtToken = jwtService.generateToken(user);
//        CookieHelper.addCookie(response, "arebbus", jwtToken);
        return new AuthResponse(user.getId(), "User Successfully Logged in", true, jwtToken);
    }
}