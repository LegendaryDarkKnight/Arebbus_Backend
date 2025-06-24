package com.project.arebbus.repository;

import com.project.arebbus.model.User;
import com.project.arebbus.repositories.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class UserRepositoryTests {
    @Autowired
    private UserRepository userRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Test
    void testFindByEmail() {
        String fake = "https://picsum.photos/seed/example/300/200";
        User user = User.builder()
                .email("test@gmail.com")
                .name("testUser")
                .password(passwordEncoder.encode("testpass"))
                .reputation(0)
                .image(fake)
                .valid(true)
                .build();

        User saved = userRepository.save(user);

        Assertions.assertThat(saved).isNotNull();
        Assertions.assertThat(userRepository.findByEmail(saved.getEmail())).isPresent();
    }
}
