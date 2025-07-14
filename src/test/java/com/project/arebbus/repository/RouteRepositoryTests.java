package com.project.arebbus.repository;

import com.project.arebbus.model.Route;
import com.project.arebbus.model.User;
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
class RouteRepositoryTests {
    @Autowired
    private RouteRepository routeRepository;

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
        Route route = Route.builder()
                .name("Test Route")
                .author(testUser)
                .build();

        Route savedRoute = routeRepository.save(route);

        List<Route> foundRoutes = routeRepository.findByAuthor(testUser);

        Assertions.assertThat(foundRoutes).isNotEmpty();
        Assertions.assertThat(foundRoutes).contains(savedRoute);
        Assertions.assertThat(foundRoutes.get(0).getAuthor().getId()).isEqualTo(testUser.getId());
    }

    @Test
    void testFindByNameContainingIgnoreCase() {
        Route route1 = Route.builder()
                .name("City Bus Route")
                .author(testUser)
                .build();

        Route route2 = Route.builder()
                .name("Express Line")
                .author(testUser)
                .build();

        Route route3 = Route.builder()
                .name("Suburban Route")
                .author(testUser)
                .build();

        routeRepository.save(route1);
        routeRepository.save(route2);
        routeRepository.save(route3);

        List<Route> foundRoutes = routeRepository.findByNameContainingIgnoreCase("route");

        Assertions.assertThat(foundRoutes).hasSize(2);
        Assertions.assertThat(foundRoutes).extracting(Route::getName)
                .containsExactlyInAnyOrder("City Bus Route", "Suburban Route");

        List<Route> foundRoutesLowerCase = routeRepository.findByNameContainingIgnoreCase("EXPRESS");

        Assertions.assertThat(foundRoutesLowerCase).hasSize(1);
        Assertions.assertThat(foundRoutesLowerCase.get(0).getName()).isEqualTo("Express Line");
    }

    @Test
    void testFindRoutesBySubscriptionCountDesc() {
        Route route1 = Route.builder()
                .name("Popular Route")
                .author(testUser)
                .build();

        Route route2 = Route.builder()
                .name("Less Popular Route")
                .author(testUser)
                .build();

        routeRepository.save(route1);
        routeRepository.save(route2);

        List<Route> foundRoutes = routeRepository.findRoutesBySubscriptionCountDesc();

        Assertions.assertThat(foundRoutes).hasSize(2);
        Assertions.assertThat(foundRoutes).extracting(Route::getName)
                .containsExactly("Popular Route", "Less Popular Route");
    }

    @Test
    void testFindByAuthorReturnsEmptyListWhenNoRoutesFound() {
        User anotherUser = User.builder()
                .email("another@gmail.com")
                .name("anotherUser")
                .password(passwordEncoder.encode("anotherpass"))
                .reputation(0)
                .image("https://picsum.photos/seed/another/300/200")
                .valid(true)
                .build();
        anotherUser = userRepository.save(anotherUser);

        List<Route> foundRoutes = routeRepository.findByAuthor(anotherUser);

        Assertions.assertThat(foundRoutes).isEmpty();
    }

    @Test
    void testFindByNameContainingIgnoreCaseReturnsEmptyListWhenNoMatch() {
        Route route = Route.builder()
                .name("Test Route")
                .author(testUser)
                .build();

        routeRepository.save(route);

        List<Route> foundRoutes = routeRepository.findByNameContainingIgnoreCase("nonexistent");

        Assertions.assertThat(foundRoutes).isEmpty();
    }

    @Test
    void testFindRoutesBySubscriptionCountDescReturnsEmptyListWhenNoRoutes() {
        List<Route> foundRoutes = routeRepository.findRoutesBySubscriptionCountDesc();

        Assertions.assertThat(foundRoutes).isEmpty();
    }
}