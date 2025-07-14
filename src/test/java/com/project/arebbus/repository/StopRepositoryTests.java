package com.project.arebbus.repository;

import com.project.arebbus.model.Stop;
import com.project.arebbus.model.User;
import com.project.arebbus.repositories.StopRepository;
import com.project.arebbus.repositories.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class StopRepositoryTests {
    @Autowired
    private StopRepository stopRepository;

    @Autowired
    private UserRepository userRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private User testUser;

    @BeforeEach
    void setUp() {
        String fake = "https://picsum.photos/seed/example/300/200";
        testUser = User.builder()
                .email("test@gmail.com")
                .name("testUser")
                .password(passwordEncoder.encode("testpass"))
                .reputation(0)
                .image(fake)
                .valid(true)
                .build();
        testUser = userRepository.save(testUser);
    }

    @Test
    void testFindByAuthor() {
        Stop stop = Stop.builder()
                .name("Test Stop")
                .latitude(new BigDecimal("40.7128"))
                .longitude(new BigDecimal("-74.0060"))
                .author(testUser)
                .build();

        Stop savedStop = stopRepository.save(stop);

        List<Stop> foundStops = stopRepository.findByAuthor(testUser);

        Assertions.assertThat(foundStops).isNotEmpty();
        Assertions.assertThat(foundStops).contains(savedStop);
        Assertions.assertThat(foundStops.get(0).getAuthor().getId()).isEqualTo(testUser.getId());
    }

    @Test
    void testFindByNameContainingIgnoreCase() {
        Stop stop1 = Stop.builder()
                .name("Central Station")
                .latitude(new BigDecimal("40.7128"))
                .longitude(new BigDecimal("-74.0060"))
                .author(testUser)
                .build();

        Stop stop2 = Stop.builder()
                .name("Main Street Stop")
                .latitude(new BigDecimal("40.7589"))
                .longitude(new BigDecimal("-73.9851"))
                .author(testUser)
                .build();

        Stop stop3 = Stop.builder()
                .name("Park Avenue")
                .latitude(new BigDecimal("40.7505"))
                .longitude(new BigDecimal("-73.9934"))
                .author(testUser)
                .build();

        stopRepository.save(stop1);
        stopRepository.save(stop2);
        stopRepository.save(stop3);

        List<Stop> foundStops = stopRepository.findByNameContainingIgnoreCase("station");

        Assertions.assertThat(foundStops).hasSize(1);
        Assertions.assertThat(foundStops.get(0).getName()).isEqualTo("Central Station");

        List<Stop> foundStopsLowerCase = stopRepository.findByNameContainingIgnoreCase("STREET");

        Assertions.assertThat(foundStopsLowerCase).hasSize(1);
        Assertions.assertThat(foundStopsLowerCase.get(0).getName()).isEqualTo("Main Street Stop");
    }

    @Test
    void testFindStopsInArea() {
        Stop stop1 = Stop.builder()
                .name("Stop 1")
                .latitude(new BigDecimal("40.7128"))
                .longitude(new BigDecimal("-74.0060"))
                .author(testUser)
                .build();

        Stop stop2 = Stop.builder()
                .name("Stop 2")
                .latitude(new BigDecimal("40.7589"))
                .longitude(new BigDecimal("-73.9851"))
                .author(testUser)
                .build();

        Stop stop3 = Stop.builder()
                .name("Stop 3")
                .latitude(new BigDecimal("41.0000"))
                .longitude(new BigDecimal("-75.0000"))
                .author(testUser)
                .build();

        stopRepository.save(stop1);
        stopRepository.save(stop2);
        stopRepository.save(stop3);

        BigDecimal minLat = new BigDecimal("40.7000");
        BigDecimal maxLat = new BigDecimal("40.8000");
        BigDecimal minLon = new BigDecimal("-74.1000");
        BigDecimal maxLon = new BigDecimal("-73.9000");

        List<Stop> foundStops = stopRepository.findStopsInArea(minLat, maxLat, minLon, maxLon);

        Assertions.assertThat(foundStops).hasSize(2);
        Assertions.assertThat(foundStops).extracting(Stop::getName)
                .containsExactlyInAnyOrder("Stop 1", "Stop 2");
    }

    @Test
    void testFindByAuthorReturnsEmptyListWhenNoStopsFound() {
        User anotherUser = User.builder()
                .email("another@gmail.com")
                .name("anotherUser")
                .password(passwordEncoder.encode("anotherpass"))
                .reputation(0)
                .image("https://picsum.photos/seed/another/300/200")
                .valid(true)
                .build();
        anotherUser = userRepository.save(anotherUser);

        List<Stop> foundStops = stopRepository.findByAuthor(anotherUser);

        Assertions.assertThat(foundStops).isEmpty();
    }

    @Test
    void testFindByNameContainingIgnoreCaseReturnsEmptyListWhenNoMatch() {
        Stop stop = Stop.builder()
                .name("Test Stop")
                .latitude(new BigDecimal("40.7128"))
                .longitude(new BigDecimal("-74.0060"))
                .author(testUser)
                .build();

        stopRepository.save(stop);

        List<Stop> foundStops = stopRepository.findByNameContainingIgnoreCase("nonexistent");

        Assertions.assertThat(foundStops).isEmpty();
    }

    @Test
    void testFindStopsInAreaReturnsEmptyListWhenNoStopsInArea() {
        Stop stop = Stop.builder()
                .name("Far Away Stop")
                .latitude(new BigDecimal("50.0000"))
                .longitude(new BigDecimal("-80.0000"))
                .author(testUser)
                .build();

        stopRepository.save(stop);

        BigDecimal minLat = new BigDecimal("40.7000");
        BigDecimal maxLat = new BigDecimal("40.8000");
        BigDecimal minLon = new BigDecimal("-74.1000");
        BigDecimal maxLon = new BigDecimal("-73.9000");

        List<Stop> foundStops = stopRepository.findStopsInArea(minLat, maxLat, minLon, maxLon);

        Assertions.assertThat(foundStops).isEmpty();
    }
}