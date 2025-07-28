package com.project.arebbus.service;

import com.project.arebbus.dto.BusCreateRequest;
import com.project.arebbus.dto.BusInstallRequest;
import com.project.arebbus.dto.BusInstallResponse;
import com.project.arebbus.dto.BusResponse;
import com.project.arebbus.dto.PagedBusResponse;
import com.project.arebbus.exception.BusAlreadyInstalledException;
import com.project.arebbus.exception.BusNotFoundException;
import com.project.arebbus.exception.BusNotInstalledException;
import com.project.arebbus.exception.RouteNotFoundException;
import com.project.arebbus.model.Bus;
import com.project.arebbus.model.Install;
import com.project.arebbus.model.InstallId;
import com.project.arebbus.model.Route;
import com.project.arebbus.model.RouteStop;
import com.project.arebbus.model.Stop;
import com.project.arebbus.model.User;
import com.project.arebbus.repositories.BusRepository;
import com.project.arebbus.repositories.BusUpvoteRepository;
import com.project.arebbus.repositories.InstallRepository;
import com.project.arebbus.repositories.RouteRepository;
import com.project.arebbus.repositories.RouteStopRepository;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BusServiceTests {

    @Mock
    private BusRepository busRepository;

    @Mock
    private RouteRepository routeRepository;

    @Mock
    private RouteStopRepository routeStopRepository;

    @Mock
    private BusUpvoteRepository busUpvoteRepository;

    @Mock
    private InstallRepository installRepository;

    @InjectMocks
    private BusService busService;

    @Test
    void testCreateBus() {
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

        BusCreateRequest request = new BusCreateRequest();
        request.setName("Test Bus");
        request.setRouteId(1L);
        request.setCapacity((short) 50);

        Bus savedBus = Bus.builder()
                .id(1L)
                .name("Test Bus")
                .author(user)
                .route(route)
                .capacity((short) 50)
                .numInstall(0)
                .numUpvote(0L)
                .build();

        when(routeRepository.findById(1L)).thenReturn(Optional.of(route));
        when(busRepository.save(any(Bus.class))).thenReturn(savedBus);
        when(routeStopRepository.findByRouteOrderByStopIndex(route)).thenReturn(Arrays.asList());
        when(busUpvoteRepository.existsByUserIdAndBusId(1L, 1L)).thenReturn(false);

        BusResponse response = busService.createBus(user, request);

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getId()).isEqualTo(1L);
        Assertions.assertThat(response.getName()).isEqualTo("Test Bus");
        Assertions.assertThat(response.getAuthorName()).isEqualTo("testUser");
        Assertions.assertThat(response.getCapacity()).isEqualTo((short) 50);
        Assertions.assertThat(response.getNumInstall()).isEqualTo(0);
        Assertions.assertThat(response.getNumUpvote()).isEqualTo(0L);
        Assertions.assertThat(response.isUpvoted()).isFalse();
    }

    @Test
    void testCreateBusThrowsExceptionWhenRouteNotFound() {
        User user = User.builder()
                .id(1L)
                .name("testUser")
                .email("test@gmail.com")
                .build();

        BusCreateRequest request = new BusCreateRequest();
        request.setName("Test Bus");
        request.setRouteId(999L);
        request.setCapacity((short) 50);

        when(routeRepository.findById(999L)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> busService.createBus(user, request))
                .isInstanceOf(RouteNotFoundException.class)
                .hasMessage("Route with id 999 not found");
    }

    @Test
    void testGetBusById() {
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

        Bus bus = Bus.builder()
                .id(1L)
                .name("Test Bus")
                .author(user)
                .route(route)
                .capacity((short) 50)
                .numInstall(10)
                .numUpvote(5L)
                .build();

        when(busRepository.findById(1L)).thenReturn(Optional.of(bus));
        when(routeStopRepository.findByRouteOrderByStopIndex(route)).thenReturn(Arrays.asList());
        when(busUpvoteRepository.existsByUserIdAndBusId(1L, 1L)).thenReturn(true);

        BusResponse response = busService.getBusById(1L, user);

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getId()).isEqualTo(1L);
        Assertions.assertThat(response.getName()).isEqualTo("Test Bus");
        Assertions.assertThat(response.getAuthorName()).isEqualTo("testUser");
        Assertions.assertThat(response.isUpvoted()).isTrue();
    }

    @Test
    void testGetBusByIdThrowsExceptionWhenNotFound() {
        User user = User.builder()
                .id(1L)
                .name("testUser")
                .email("test@gmail.com")
                .build();

        when(busRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> busService.getBusById(1L, user))
                .isInstanceOf(BusNotFoundException.class)
                .hasMessage("Bus with id 1 not found");
    }

    @Test
    void testGetAllBuses() {
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

        Bus bus1 = Bus.builder()
                .id(1L)
                .name("Bus 1")
                .author(user)
                .route(route)
                .capacity((short) 50)
                .numInstall(0)
                .numUpvote(0L)
                .build();

        Bus bus2 = Bus.builder()
                .id(2L)
                .name("Bus 2")
                .author(user)
                .route(route)
                .capacity((short) 60)
                .numInstall(0)
                .numUpvote(0L)
                .build();

        List<Bus> buses = Arrays.asList(bus1, bus2);
        Page<Bus> busPage = new PageImpl<>(buses, PageRequest.of(0, 10), 2);

        when(busRepository.findAll(any(PageRequest.class))).thenReturn(busPage);
        when(routeStopRepository.findByRouteOrderByStopIndex(route)).thenReturn(Arrays.asList());
        when(busUpvoteRepository.existsByUserIdAndBusId(1L, 1L)).thenReturn(false);
        when(busUpvoteRepository.existsByUserIdAndBusId(1L, 2L)).thenReturn(false);

        PagedBusResponse response = busService.getAllBuses(user, 0, 10);

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getBuses()).hasSize(2);
        Assertions.assertThat(response.getPage()).isEqualTo(0);
        Assertions.assertThat(response.getSize()).isEqualTo(10);
        Assertions.assertThat(response.getTotalPages()).isEqualTo(1);
        Assertions.assertThat(response.getTotalElements()).isEqualTo(2);
    }

    @Test
    void testInstallBus() {
        User user = User.builder()
                .id(1L)
                .name("testUser")
                .email("test@gmail.com")
                .build();

        Bus bus = Bus.builder()
                .id(1L)
                .name("Test Bus")
                .numInstall(5)
                .build();

        BusInstallRequest request = new BusInstallRequest();
        request.setBusId(1L);

        Install install = Install.builder()
                .userId(1L)
                .busId(1L)
                .user(user)
                .bus(bus)
                .build();

        when(busRepository.findById(1L)).thenReturn(Optional.of(bus));
        when(installRepository.existsByUserAndBus(user, bus)).thenReturn(false);
        when(installRepository.save(any(Install.class))).thenReturn(install);
        when(busRepository.save(any(Bus.class))).thenReturn(bus);

        BusInstallResponse response = busService.installBus(user, request);

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getBusId()).isEqualTo(1L);
        Assertions.assertThat(response.getBusName()).isEqualTo("Test Bus");
        Assertions.assertThat(response.getMessage()).isEqualTo("Bus installed successfully");
        Assertions.assertThat(response.isInstalled()).isTrue();

        verify(busRepository).save(bus);
        Assertions.assertThat(bus.getNumInstall()).isEqualTo(6);
    }

    @Test
    void testInstallBusThrowsExceptionWhenAlreadyInstalled() {
        User user = User.builder()
                .id(1L)
                .name("testUser")
                .email("test@gmail.com")
                .build();

        Bus bus = Bus.builder()
                .id(1L)
                .name("Test Bus")
                .numInstall(5)
                .build();

        BusInstallRequest request = new BusInstallRequest();
        request.setBusId(1L);

        when(busRepository.findById(1L)).thenReturn(Optional.of(bus));
        when(installRepository.existsByUserAndBus(user, bus)).thenReturn(true);

        Assertions.assertThatThrownBy(() -> busService.installBus(user, request))
                .isInstanceOf(BusAlreadyInstalledException.class)
                .hasMessage("Bus with id 1 is already installed");
    }

    @Test
    void testUninstallBus() {
        User user = User.builder()
                .id(1L)
                .name("testUser")
                .email("test@gmail.com")
                .build();

        Bus bus = Bus.builder()
                .id(1L)
                .name("Test Bus")
                .numInstall(5)
                .build();

        BusInstallRequest request = new BusInstallRequest();
        request.setBusId(1L);

        when(busRepository.findById(1L)).thenReturn(Optional.of(bus));
        when(installRepository.existsByUserAndBus(user, bus)).thenReturn(true);
        when(busRepository.save(any(Bus.class))).thenReturn(bus);

        BusInstallResponse response = busService.uninstallBus(user, request);

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getBusId()).isEqualTo(1L);
        Assertions.assertThat(response.getBusName()).isEqualTo("Test Bus");
        Assertions.assertThat(response.getMessage()).isEqualTo("Bus uninstalled successfully");
        Assertions.assertThat(response.isInstalled()).isFalse();

        verify(installRepository).deleteById(new InstallId(1L, 1L));
        verify(busRepository).save(bus);
        Assertions.assertThat(bus.getNumInstall()).isEqualTo(4);
    }

    @Test
    void testUninstallBusThrowsExceptionWhenNotInstalled() {
        User user = User.builder()
                .id(1L)
                .name("testUser")
                .email("test@gmail.com")
                .build();

        Bus bus = Bus.builder()
                .id(1L)
                .name("Test Bus")
                .numInstall(5)
                .build();

        BusInstallRequest request = new BusInstallRequest();
        request.setBusId(1L);

        when(busRepository.findById(1L)).thenReturn(Optional.of(bus));
        when(installRepository.existsByUserAndBus(user, bus)).thenReturn(false);

        Assertions.assertThatThrownBy(() -> busService.uninstallBus(user, request))
                .isInstanceOf(BusNotInstalledException.class)
                .hasMessage("Bus with id 1 is not installed");
    }

    @Test
    void testGetInstalledBuses() {
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

        Bus bus1 = Bus.builder()
                .id(1L)
                .name("Installed Bus 1")
                .author(user)
                .route(route)
                .capacity((short) 50)
                .numInstall(0)
                .numUpvote(0L)
                .build();

        Bus bus2 = Bus.builder()
                .id(2L)
                .name("Installed Bus 2")
                .author(user)
                .route(route)
                .capacity((short) 60)
                .numInstall(0)
                .numUpvote(0L)
                .build();

        Install install1 = Install.builder()
                .userId(1L)
                .busId(1L)
                .user(user)
                .bus(bus1)
                .build();

        Install install2 = Install.builder()
                .userId(1L)
                .busId(2L)
                .user(user)
                .bus(bus2)
                .build();

        List<Bus> buses = Arrays.asList(bus1, bus2);
        Page<Bus> busPage = new PageImpl<>(buses, PageRequest.of(0, 10), 2);
        
        when(busRepository.findBusesInstalledByUser(eq(user), any(PageRequest.class))).thenReturn(busPage);
        when(routeStopRepository.findByRouteOrderByStopIndex(route)).thenReturn(Arrays.asList());
        when(busUpvoteRepository.existsByUserIdAndBusId(1L, 1L)).thenReturn(false);
        when(busUpvoteRepository.existsByUserIdAndBusId(1L, 2L)).thenReturn(false);

        PagedBusResponse response = busService.getInstalledBuses(user, 0, 10);

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getBuses()).hasSize(2);
        Assertions.assertThat(response.getPage()).isEqualTo(0);
        Assertions.assertThat(response.getSize()).isEqualTo(10);
        Assertions.assertThat(response.getTotalPages()).isEqualTo(1);
        Assertions.assertThat(response.getTotalElements()).isEqualTo(2);
        Assertions.assertThat(response.getBuses().get(0).getName()).isEqualTo("Installed Bus 1");
        Assertions.assertThat(response.getBuses().get(1).getName()).isEqualTo("Installed Bus 2");
    }

    @Test
    void testGetInstalledBusesWithPagination() {
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

        Bus bus1 = Bus.builder()
                .id(1L)
                .name("Bus 1")
                .author(user)
                .route(route)
                .capacity((short) 50)
                .numInstall(0)
                .numUpvote(0L)
                .build();

        Bus bus2 = Bus.builder()
                .id(2L)
                .name("Bus 2")
                .author(user)
                .route(route)
                .capacity((short) 60)
                .numInstall(0)
                .numUpvote(0L)
                .build();

        Bus bus3 = Bus.builder()
                .id(3L)
                .name("Bus 3")
                .author(user)
                .route(route)
                .capacity((short) 70)
                .numInstall(0)
                .numUpvote(0L)
                .build();

        List<Bus> buses = Arrays.asList(bus1, bus2);
        Page<Bus> busPage = new PageImpl<>(buses, PageRequest.of(0, 2), 3);
        
        when(busRepository.findBusesInstalledByUser(eq(user), any(PageRequest.class))).thenReturn(busPage);
        when(routeStopRepository.findByRouteOrderByStopIndex(route)).thenReturn(Arrays.asList());
        when(busUpvoteRepository.existsByUserIdAndBusId(1L, 1L)).thenReturn(false);
        when(busUpvoteRepository.existsByUserIdAndBusId(1L, 2L)).thenReturn(false);

        PagedBusResponse response = busService.getInstalledBuses(user, 0, 2);

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getBuses()).hasSize(2);
        Assertions.assertThat(response.getPage()).isEqualTo(0);
        Assertions.assertThat(response.getSize()).isEqualTo(2);
        Assertions.assertThat(response.getTotalPages()).isEqualTo(2);
        Assertions.assertThat(response.getTotalElements()).isEqualTo(3);
    }
}