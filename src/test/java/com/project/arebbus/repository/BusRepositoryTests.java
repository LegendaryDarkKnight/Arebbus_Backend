package com.project.arebbus.repository;

import com.project.arebbus.model.Bus;
import com.project.arebbus.model.Route;
import com.project.arebbus.model.User;
import com.project.arebbus.repositories.BusRepository;
import com.project.arebbus.repositories.RouteRepository;
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

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class BusRepositoryTests {
    @Autowired
    private BusRepository busRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RouteRepository routeRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private User testUser;
    private Route testRoute;

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

        testRoute = Route.builder()
                .name("Test Route")
                .author(testUser)
                .build();
        testRoute = routeRepository.save(testRoute);
    }

    @Test
    void testFindByAuthor() {
        Bus bus = Bus.builder()
                .name("Test Bus")
                .author(testUser)
                .route(testRoute)
                .capacity((short) 50)
                .numInstall(0)
                .numUpvote(0L)
                .build();

        Bus savedBus = busRepository.save(bus);

        List<Bus> foundBuses = busRepository.findByAuthor(testUser);

        Assertions.assertThat(foundBuses).isNotEmpty();
        Assertions.assertThat(foundBuses).contains(savedBus);
        Assertions.assertThat(foundBuses.get(0).getAuthor().getId()).isEqualTo(testUser.getId());
    }

    @Test
    void testFindByRoute() {
        Bus bus1 = Bus.builder()
                .name("Bus 1")
                .author(testUser)
                .route(testRoute)
                .capacity((short) 40)
                .numInstall(0)
                .numUpvote(0L)
                .build();

        Bus bus2 = Bus.builder()
                .name("Bus 2")
                .author(testUser)
                .route(testRoute)
                .capacity((short) 60)
                .numInstall(0)
                .numUpvote(0L)
                .build();

        busRepository.save(bus1);
        busRepository.save(bus2);

        List<Bus> foundBuses = busRepository.findByRoute(testRoute);

        Assertions.assertThat(foundBuses).hasSize(2);
        Assertions.assertThat(foundBuses).extracting(Bus::getName)
                .containsExactlyInAnyOrder("Bus 1", "Bus 2");
    }

    @Test
    void testFindByStatus() {
        Bus bus1 = Bus.builder()
                .name("Active Bus")
                .author(testUser)
                .route(testRoute)
                .capacity((short) 50)
                .numInstall(0)
                .numUpvote(0L)
                .status("ACTIVE")
                .build();

        Bus bus2 = Bus.builder()
                .name("Inactive Bus")
                .author(testUser)
                .route(testRoute)
                .capacity((short) 50)
                .numInstall(0)
                .numUpvote(0L)
                .status("INACTIVE")
                .build();

        busRepository.save(bus1);
        busRepository.save(bus2);

        List<Bus> activeBuses = busRepository.findByStatus("ACTIVE");

        Assertions.assertThat(activeBuses).hasSize(1);
        Assertions.assertThat(activeBuses.get(0).getName()).isEqualTo("Active Bus");
    }

    @Test
    void testFindByCapacityGreaterThan() {
        Bus smallBus = Bus.builder()
                .name("Small Bus")
                .author(testUser)
                .route(testRoute)
                .capacity((short) 30)
                .numInstall(0)
                .numUpvote(0L)
                .build();

        Bus largeBus = Bus.builder()
                .name("Large Bus")
                .author(testUser)
                .route(testRoute)
                .capacity((short) 70)
                .numInstall(0)
                .numUpvote(0L)
                .build();

        busRepository.save(smallBus);
        busRepository.save(largeBus);

        List<Bus> largeBuses = busRepository.findByCapacityGreaterThan((short) 50);

        Assertions.assertThat(largeBuses).hasSize(1);
        Assertions.assertThat(largeBuses.get(0).getName()).isEqualTo("Large Bus");
        Assertions.assertThat(largeBuses.get(0).getCapacity()).isEqualTo((short) 70);
    }

    @Test
    void testFindBusesByInstallCountDesc() {
        Bus popularBus = Bus.builder()
                .name("Popular Bus")
                .author(testUser)
                .route(testRoute)
                .capacity((short) 50)
                .numInstall(100)
                .numUpvote(0L)
                .build();

        Bus unpopularBus = Bus.builder()
                .name("Unpopular Bus")
                .author(testUser)
                .route(testRoute)
                .capacity((short) 50)
                .numInstall(5)
                .numUpvote(0L)
                .build();

        busRepository.save(popularBus);
        busRepository.save(unpopularBus);

        List<Bus> sortedBuses = busRepository.findBusesByInstallCountDesc();

        Assertions.assertThat(sortedBuses).hasSize(2);
        Assertions.assertThat(sortedBuses.get(0).getName()).isEqualTo("Popular Bus");
        Assertions.assertThat(sortedBuses.get(1).getName()).isEqualTo("Unpopular Bus");
    }

    @Test
    void testFindBusesByUpvoteCountDesc() {
        Bus highlyUpvotedBus = Bus.builder()
                .name("Highly Upvoted Bus")
                .author(testUser)
                .route(testRoute)
                .capacity((short) 50)
                .numInstall(0)
                .numUpvote(200L)
                .build();

        Bus lowUpvotedBus = Bus.builder()
                .name("Low Upvoted Bus")
                .author(testUser)
                .route(testRoute)
                .capacity((short) 50)
                .numInstall(0)
                .numUpvote(10L)
                .build();

        busRepository.save(highlyUpvotedBus);
        busRepository.save(lowUpvotedBus);

        List<Bus> sortedBuses = busRepository.findBusesByUpvoteCountDesc();

        Assertions.assertThat(sortedBuses).hasSize(2);
        Assertions.assertThat(sortedBuses.get(0).getName()).isEqualTo("Highly Upvoted Bus");
        Assertions.assertThat(sortedBuses.get(1).getName()).isEqualTo("Low Upvoted Bus");
    }

    @Test
    void testFindByAuthorReturnsEmptyListWhenNoBusesFound() {
        User anotherUser = User.builder()
                .email("another@gmail.com")
                .name("anotherUser")
                .password(passwordEncoder.encode("anotherpass"))
                .reputation(0)
                .image("https://picsum.photos/seed/another/300/200")
                .valid(true)
                .build();
        anotherUser = userRepository.save(anotherUser);

        List<Bus> foundBuses = busRepository.findByAuthor(anotherUser);

        Assertions.assertThat(foundBuses).isEmpty();
    }

    @Test
    void testFindByStatusReturnsEmptyListWhenNoMatch() {
        Bus bus = Bus.builder()
                .name("Test Bus")
                .author(testUser)
                .route(testRoute)
                .capacity((short) 50)
                .numInstall(0)
                .numUpvote(0L)
                .status("ACTIVE")
                .build();

        busRepository.save(bus);

        List<Bus> foundBuses = busRepository.findByStatus("NONEXISTENT");

        Assertions.assertThat(foundBuses).isEmpty();
    }
}