package com.project.arebbus.service;

import com.project.arebbus.dto.AuthResponse;
import com.project.arebbus.dto.UserLoginDTO;
import com.project.arebbus.dto.UserRegistrationDTO;
import com.project.arebbus.exception.UserAlreadyExists;
import com.project.arebbus.model.User;
import com.project.arebbus.repositories.UserRepository;
import com.project.arebbus.utils.CookieHelper;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    public AuthResponse register(UserRegistrationDTO input,
                                 HttpServletResponse response) {
        /*
        * Assuming the fields in request body is non-empty
        * */
        if(userRepository.existsByEmail(input.getEmail())){
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
        var jwtToken = jwtService.generateToken(user);
        CookieHelper.addCookie(response,"arebbus",jwtToken);
        return new AuthResponse(user.getId(), "User Registered Successfully", true);
    }

    public AuthResponse authenticate(UserLoginDTO input,
                                     HttpServletResponse response) {
        var user = userRepository.findByEmail(input.getEmail())
                .orElse(null);
        if(user == null)
            return new AuthResponse(0L, "User Not Found", false);

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );
        String jwtToken = jwtService.generateToken(user);
        CookieHelper.addCookie(response, "arebbus", jwtToken);
        return new AuthResponse(user.getId(), "User Successfully Logged in", true);
    }
}