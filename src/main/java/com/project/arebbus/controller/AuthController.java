package com.project.arebbus.controller;
import com.project.arebbus.dto.AuthResponse;
import com.project.arebbus.dto.UserLoginRequest;
import com.project.arebbus.dto.UserRegistrationRequest;
import com.project.arebbus.service.AuthService;
import com.project.arebbus.service.JwtService;
import com.project.arebbus.utils.CookieHelper;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final JwtService jwtService;
    private final AuthService authService;

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    @GetMapping("/test")
    public String getMethodName() {
        return "In auth";
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody UserRegistrationRequest request, HttpServletResponse response1) {
        AuthResponse response = authService.register(request);
        if (response.isSuccess()) {
            CookieHelper.addCookie(response1, "arebbus", response.getToken());
            response.setToken(null);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody UserLoginRequest request, HttpServletResponse response1) {
        AuthResponse response = authService.authenticate(request);
        if (response.isSuccess()) {
            CookieHelper.addCookie(response1, "arebbus", response.getToken());
            response.setToken(null);
        }
        return ResponseEntity.ok(response);
    }
}
