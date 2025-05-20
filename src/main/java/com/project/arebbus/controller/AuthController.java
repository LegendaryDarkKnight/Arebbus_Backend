package com.project.arebbus.controller;
import com.project.arebbus.dto.AuthResponse;
import com.project.arebbus.dto.UserLoginDTO;
import com.project.arebbus.dto.UserRegistrationDTO;
import com.project.arebbus.service.AuthService;
import com.project.arebbus.service.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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

    @GetMapping("/test")
    public String getMethodName() {
        return "In auth";
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody UserRegistrationDTO request, HttpServletResponse response) {
        return ResponseEntity.ok(authService.register(request,response));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody UserLoginDTO request,HttpServletResponse response) {
        return ResponseEntity.ok(authService.authenticate(request,response));
    }
}
