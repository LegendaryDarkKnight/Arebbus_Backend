package com.project.arebbus.service;

import com.project.arebbus.dto.PagedRouteResponse;
import com.project.arebbus.dto.RouteCreateRequest;
import com.project.arebbus.dto.RouteResponse;
import com.project.arebbus.dto.StopResponse;
import com.project.arebbus.exception.RouteNotFoundException;
import com.project.arebbus.exception.StopNotFoundException;
import com.project.arebbus.model.Route;
import com.project.arebbus.model.RouteStop;
import com.project.arebbus.model.Stop;
import com.project.arebbus.model.User;
import com.project.arebbus.repositories.RouteRepository;
import com.project.arebbus.repositories.RouteStopRepository;
import com.project.arebbus.repositories.StopRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RouteServiceTests {

    @Mock
    private RouteRepository routeRepository;

    @Mock
    private RouteStopRepository routeStopRepository;

    @Mock
    private StopRepository stopRepository;

    @InjectMocks
    private RouteService routeService;

    @Test
    void testCreateRoute() {
        User user = User.builder()
                .id(1L)
                .name("testUser")
                .email("test@gmail.com")
                .build();

        Stop stop1 = Stop.builder()
                .id(1L)
                .name("Stop 1")
                .latitude(new BigDecimal("40.7128"))
                .longitude(new BigDecimal("-74.0060"))
                .author(user)
                .build();

        Stop stop2 = Stop.builder()
                .id(2L)
                .name("Stop 2")
                .latitude(new BigDecimal("40.7589"))
                .longitude(new BigDecimal("-73.9851"))
                .author(user)
                .build();

        RouteCreateRequest request = new RouteCreateRequest();
        request.setName("Test Route");
        request.setStopIds(Arrays.asList(1L, 2L));

        Route savedRoute = Route.builder()
                .id(1L)
                .name("Test Route")
                .author(user)
                .build();

        when(routeRepository.save(any(Route.class))).thenReturn(savedRoute);
        when(stopRepository.findById(1L)).thenReturn(Optional.of(stop1));
        when(stopRepository.findById(2L)).thenReturn(Optional.of(stop2));
        when(routeStopRepository.save(any(RouteStop.class))).thenReturn(new RouteStop());

        RouteResponse response = routeService.createRoute(user, request);

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getId()).isEqualTo(1L);
        Assertions.assertThat(response.getName()).isEqualTo("Test Route");
        Assertions.assertThat(response.getAuthorName()).isEqualTo("testUser");
        Assertions.assertThat(response.getStops()).hasSize(2);
        Assertions.assertThat(response.getStops().get(0).getName()).isEqualTo("Stop 1");
        Assertions.assertThat(response.getStops().get(1).getName()).isEqualTo("Stop 2");
    }

    @Test
    void testCreateRouteThrowsExceptionWhenStopNotFound() {
        User user = User.builder()
                .id(1L)
                .name("testUser")
                .email("test@gmail.com")
                .build();

        RouteCreateRequest request = new RouteCreateRequest();
        request.setName("Test Route");
        request.setStopIds(Arrays.asList(999L));

        Route savedRoute = Route.builder()
                .id(1L)
                .name("Test Route")
                .author(user)
                .build();

        when(routeRepository.save(any(Route.class))).thenReturn(savedRoute);
        when(stopRepository.findById(999L)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> routeService.createRoute(user, request))
                .isInstanceOf(StopNotFoundException.class)
                .hasMessage("Stop with id 999 not found");
    }

    @Test
    void testGetRouteById() {
        User user = User.builder()
                .id(1L)
                .name("testUser")
                .email("test@gmail.com")
                .build();

        Stop stop1 = Stop.builder()
                .id(1L)
                .name("Stop 1")
                .latitude(new BigDecimal("40.7128"))
                .longitude(new BigDecimal("-74.0060"))
                .author(user)
                .build();

        Route route = Route.builder()
                .id(1L)
                .name("Test Route")
                .author(user)
                .build();

        RouteStop routeStop = RouteStop.builder()
                .routeId(1L)
                .stopId(1L)
                .stopIndex(0L)
                .route(route)
                .stop(stop1)
                .build();

        when(routeRepository.findById(1L)).thenReturn(Optional.of(route));
        when(routeStopRepository.findByRouteOrderByStopIndex(route)).thenReturn(Arrays.asList(routeStop));

        RouteResponse response = routeService.getRouteById(1L);

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getId()).isEqualTo(1L);
        Assertions.assertThat(response.getName()).isEqualTo("Test Route");
        Assertions.assertThat(response.getAuthorName()).isEqualTo("testUser");
        Assertions.assertThat(response.getStops()).hasSize(1);
        Assertions.assertThat(response.getStops().get(0).getName()).isEqualTo("Stop 1");
    }

    @Test
    void testGetRouteByIdThrowsExceptionWhenNotFound() {
        when(routeRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> routeService.getRouteById(1L))
                .isInstanceOf(RouteNotFoundException.class)
                .hasMessage("Route with id 1 not found");
    }

    @Test
    void testGetAllRoutes() {
        User user1 = User.builder()
                .id(1L)
                .name("testUser1")
                .email("test1@gmail.com")
                .build();

        User user2 = User.builder()
                .id(2L)
                .name("testUser2")
                .email("test2@gmail.com")
                .build();

        Stop stop1 = Stop.builder()
                .id(1L)
                .name("Stop 1")
                .latitude(new BigDecimal("40.7128"))
                .longitude(new BigDecimal("-74.0060"))
                .author(user1)
                .build();

        Route route1 = Route.builder()
                .id(1L)
                .name("Test Route 1")
                .author(user1)
                .build();

        Route route2 = Route.builder()
                .id(2L)
                .name("Test Route 2")
                .author(user2)
                .build();

        RouteStop routeStop1 = RouteStop.builder()
                .routeId(1L)
                .stopId(1L)
                .stopIndex(0L)
                .route(route1)
                .stop(stop1)
                .build();

        List<Route> routes = Arrays.asList(route1, route2);
        Page<Route> routePage = new PageImpl<>(routes, PageRequest.of(0, 10), 2);

        when(routeRepository.findAll(eq(PageRequest.of(0, 10)))).thenReturn(routePage);
        when(routeStopRepository.findByRouteOrderByStopIndex(route1)).thenReturn(Arrays.asList(routeStop1));
        when(routeStopRepository.findByRouteOrderByStopIndex(route2)).thenReturn(Arrays.asList());

        PagedRouteResponse response = routeService.getAllRoutes(0, 10);

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getRoutes()).hasSize(2);
        Assertions.assertThat(response.getPage()).isEqualTo(0);
        Assertions.assertThat(response.getSize()).isEqualTo(10);
        Assertions.assertThat(response.getTotalPages()).isEqualTo(1);
        Assertions.assertThat(response.getTotalElements()).isEqualTo(2);

        RouteResponse firstRoute = response.getRoutes().get(0);
        Assertions.assertThat(firstRoute.getId()).isEqualTo(1L);
        Assertions.assertThat(firstRoute.getName()).isEqualTo("Test Route 1");
        Assertions.assertThat(firstRoute.getAuthorName()).isEqualTo("testUser1");
        Assertions.assertThat(firstRoute.getStops()).hasSize(1);

        RouteResponse secondRoute = response.getRoutes().get(1);
        Assertions.assertThat(secondRoute.getId()).isEqualTo(2L);
        Assertions.assertThat(secondRoute.getName()).isEqualTo("Test Route 2");
        Assertions.assertThat(secondRoute.getAuthorName()).isEqualTo("testUser2");
        Assertions.assertThat(secondRoute.getStops()).isEmpty();
    }

    @Test
    void testGetAllRoutesWithEmptyResult() {
        Page<Route> emptyPage = new PageImpl<>(Arrays.asList(), PageRequest.of(0, 10), 0);

        when(routeRepository.findAll(eq(PageRequest.of(0, 10)))).thenReturn(emptyPage);

        PagedRouteResponse response = routeService.getAllRoutes(0, 10);

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getRoutes()).isEmpty();
        Assertions.assertThat(response.getPage()).isEqualTo(0);
        Assertions.assertThat(response.getSize()).isEqualTo(10);
        Assertions.assertThat(response.getTotalPages()).isEqualTo(0);
        Assertions.assertThat(response.getTotalElements()).isEqualTo(0);
    }

    @Test
    void testGetAllRoutesWithPagination() {
        User user = User.builder()
                .id(1L)
                .name("testUser")
                .email("test@gmail.com")
                .build();

        Route route = Route.builder()
                .id(1L)
                .name("Test Route")
                .author(user)
                .build();

        List<Route> routes = Arrays.asList(route);
        Page<Route> routePage = new PageImpl<>(routes, PageRequest.of(1, 5), 10);

        when(routeRepository.findAll(eq(PageRequest.of(1, 5)))).thenReturn(routePage);
        when(routeStopRepository.findByRouteOrderByStopIndex(route)).thenReturn(Arrays.asList());

        PagedRouteResponse response = routeService.getAllRoutes(1, 5);

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getRoutes()).hasSize(1);
        Assertions.assertThat(response.getPage()).isEqualTo(1);
        Assertions.assertThat(response.getSize()).isEqualTo(5);
        Assertions.assertThat(response.getTotalPages()).isEqualTo(2);
        Assertions.assertThat(response.getTotalElements()).isEqualTo(10);
    }
}