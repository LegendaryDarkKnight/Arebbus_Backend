package com.project.arebbus.service;

import com.project.arebbus.dto.PagedStopResponse;
import com.project.arebbus.dto.StopCreateRequest;
import com.project.arebbus.dto.StopResponse;
import com.project.arebbus.exception.StopNotFoundException;
import com.project.arebbus.model.Stop;
import com.project.arebbus.model.User;
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
class StopServiceTests {

    @Mock
    private StopRepository stopRepository;

    @InjectMocks
    private StopService stopService;

    @Test
    void testCreateStop() {
        User user = User.builder()
                .id(1L)
                .name("testUser")
                .email("test@gmail.com")
                .build();

        StopCreateRequest request = new StopCreateRequest();
        request.setName("Test Stop");
        request.setLatitude(new BigDecimal("40.7128"));
        request.setLongitude(new BigDecimal("-74.0060"));

        Stop savedStop = Stop.builder()
                .id(1L)
                .name("Test Stop")
                .latitude(new BigDecimal("40.7128"))
                .longitude(new BigDecimal("-74.0060"))
                .author(user)
                .build();

        when(stopRepository.save(any(Stop.class))).thenReturn(savedStop);

        StopResponse response = stopService.createStop(user, request);

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getId()).isEqualTo(1L);
        Assertions.assertThat(response.getName()).isEqualTo("Test Stop");
        Assertions.assertThat(response.getLatitude()).isEqualTo(new BigDecimal("40.7128"));
        Assertions.assertThat(response.getLongitude()).isEqualTo(new BigDecimal("-74.0060"));
        Assertions.assertThat(response.getAuthorName()).isEqualTo("testUser");
    }

    @Test
    void testGetStopById() {
        User user = User.builder()
                .id(1L)
                .name("testUser")
                .email("test@gmail.com")
                .build();

        Stop stop = Stop.builder()
                .id(1L)
                .name("Test Stop")
                .latitude(new BigDecimal("40.7128"))
                .longitude(new BigDecimal("-74.0060"))
                .author(user)
                .build();

        when(stopRepository.findById(1L)).thenReturn(Optional.of(stop));

        StopResponse response = stopService.getStopById(1L);

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getId()).isEqualTo(1L);
        Assertions.assertThat(response.getName()).isEqualTo("Test Stop");
        Assertions.assertThat(response.getLatitude()).isEqualTo(new BigDecimal("40.7128"));
        Assertions.assertThat(response.getLongitude()).isEqualTo(new BigDecimal("-74.0060"));
        Assertions.assertThat(response.getAuthorName()).isEqualTo("testUser");
    }

    @Test
    void testGetStopByIdThrowsExceptionWhenNotFound() {
        when(stopRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> stopService.getStopById(1L))
                .isInstanceOf(StopNotFoundException.class)
                .hasMessage("Stop with id 1 not found");
    }

    @Test
    void testGetAllStops() {
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
                .name("Test Stop 1")
                .latitude(new BigDecimal("40.7128"))
                .longitude(new BigDecimal("-74.0060"))
                .author(user1)
                .build();

        Stop stop2 = Stop.builder()
                .id(2L)
                .name("Test Stop 2")
                .latitude(new BigDecimal("40.7589"))
                .longitude(new BigDecimal("-73.9851"))
                .author(user2)
                .build();

        List<Stop> stops = Arrays.asList(stop1, stop2);
        Page<Stop> stopPage = new PageImpl<>(stops, PageRequest.of(0, 10), 2);

        when(stopRepository.findAll(eq(PageRequest.of(0, 10)))).thenReturn(stopPage);

        PagedStopResponse response = stopService.getAllStops(0, 10);

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStops()).hasSize(2);
        Assertions.assertThat(response.getPage()).isEqualTo(0);
        Assertions.assertThat(response.getSize()).isEqualTo(10);
        Assertions.assertThat(response.getTotalPages()).isEqualTo(1);
        Assertions.assertThat(response.getTotalElements()).isEqualTo(2);

        StopResponse firstStop = response.getStops().get(0);
        Assertions.assertThat(firstStop.getId()).isEqualTo(1L);
        Assertions.assertThat(firstStop.getName()).isEqualTo("Test Stop 1");
        Assertions.assertThat(firstStop.getAuthorName()).isEqualTo("testUser1");

        StopResponse secondStop = response.getStops().get(1);
        Assertions.assertThat(secondStop.getId()).isEqualTo(2L);
        Assertions.assertThat(secondStop.getName()).isEqualTo("Test Stop 2");
        Assertions.assertThat(secondStop.getAuthorName()).isEqualTo("testUser2");
    }

    @Test
    void testGetAllStopsWithEmptyResult() {
        Page<Stop> emptyPage = new PageImpl<>(Arrays.asList(), PageRequest.of(0, 10), 0);

        when(stopRepository.findAll(eq(PageRequest.of(0, 10)))).thenReturn(emptyPage);

        PagedStopResponse response = stopService.getAllStops(0, 10);

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStops()).isEmpty();
        Assertions.assertThat(response.getPage()).isEqualTo(0);
        Assertions.assertThat(response.getSize()).isEqualTo(10);
        Assertions.assertThat(response.getTotalPages()).isEqualTo(0);
        Assertions.assertThat(response.getTotalElements()).isEqualTo(0);
    }

    @Test
    void testGetAllStopsWithPagination() {
        User user = User.builder()
                .id(1L)
                .name("testUser")
                .email("test@gmail.com")
                .build();

        Stop stop = Stop.builder()
                .id(1L)
                .name("Test Stop")
                .latitude(new BigDecimal("40.7128"))
                .longitude(new BigDecimal("-74.0060"))
                .author(user)
                .build();

        List<Stop> stops = Arrays.asList(stop);
        Page<Stop> stopPage = new PageImpl<>(stops, PageRequest.of(1, 5), 10);

        when(stopRepository.findAll(eq(PageRequest.of(1, 5)))).thenReturn(stopPage);

        PagedStopResponse response = stopService.getAllStops(1, 5);

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStops()).hasSize(1);
        Assertions.assertThat(response.getPage()).isEqualTo(1);
        Assertions.assertThat(response.getSize()).isEqualTo(5);
        Assertions.assertThat(response.getTotalPages()).isEqualTo(2);
        Assertions.assertThat(response.getTotalElements()).isEqualTo(10);
    }
}