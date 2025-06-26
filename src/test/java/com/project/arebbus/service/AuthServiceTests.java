package com.project.arebbus.service;

import com.project.arebbus.dto.AuthResponse;
import com.project.arebbus.dto.UserRegistrationRequest;
import com.project.arebbus.model.User;
import com.project.arebbus.repositories.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

@ExtendWith(MockitoExtension.class)
class AuthServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;


    @Test
    void testRegister() {
        String fake = "https://picsum.photos/seed/example/300/200";
        User user = User.builder()
                .email("test@gmail.com")
                .name("testUser")
                .password(passwordEncoder.encode("testpass"))
                .reputation(0)
                .image(fake)
                .valid(true)
                .build();
        UserRegistrationRequest request = new UserRegistrationRequest()
                .builder()
                .name(user.getName())
                .email(user.getEmail())
                .password(user.getPassword())
                .build();

        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(any(User.class))).thenReturn("mock.jwt.token");

        AuthResponse response = authService.register(request);

        Assertions.assertThat(response.isSuccess()).isTrue();
        Assertions.assertThat(response.getToken()).isNotNull();
    }
}
