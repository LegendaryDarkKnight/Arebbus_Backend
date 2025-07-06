package com.project.arebbus.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.arebbus.dto.PagedStopResponse;
import com.project.arebbus.dto.StopCreateRequest;
import com.project.arebbus.dto.StopResponse;
import com.project.arebbus.exception.StopNotFoundException;
import com.project.arebbus.model.User;
import com.project.arebbus.service.JwtService;
import com.project.arebbus.service.StopService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StopController.class)
class StopControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StopService stopService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "test@gmail.com")
    void testCreateStop() throws Exception {
        User user = User.builder()
                .id(1L)
                .name("testUser")
                .email("test@gmail.com")
                .build();

        StopCreateRequest request = new StopCreateRequest();
        request.setName("Test Stop");
        request.setLatitude(new BigDecimal("40.7128"));
        request.setLongitude(new BigDecimal("-74.0060"));

        StopResponse response = StopResponse.builder()
                .id(1L)
                .name("Test Stop")
                .latitude(new BigDecimal("40.7128"))
                .longitude(new BigDecimal("-74.0060"))
                .authorName("testUser")
                .build();

        when(userDetailsService.loadUserByUsername("test@gmail.com")).thenReturn(user);
        when(stopService.createStop(any(User.class), any(StopCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/stop/create")
                        .with(csrf())
                        .with(user(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Stop"))
                .andExpect(jsonPath("$.latitude").value(40.7128))
                .andExpect(jsonPath("$.longitude").value(-74.0060))
                .andExpect(jsonPath("$.authorName").value("testUser"));
    }

    @Test
    @WithMockUser
    void testGetStopById() throws Exception {
        StopResponse response = StopResponse.builder()
                .id(1L)
                .name("Test Stop")
                .latitude(new BigDecimal("40.7128"))
                .longitude(new BigDecimal("-74.0060"))
                .authorName("testUser")
                .build();

        when(stopService.getStopById(1L)).thenReturn(response);

        mockMvc.perform(get("/stop")
                        .param("stopId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Stop"))
                .andExpect(jsonPath("$.latitude").value(40.7128))
                .andExpect(jsonPath("$.longitude").value(-74.0060))
                .andExpect(jsonPath("$.authorName").value("testUser"));
    }

    @Test
    @WithMockUser
    void testGetStopByIdNotFound() throws Exception {
        when(stopService.getStopById(999L)).thenThrow(new StopNotFoundException(999L));

        mockMvc.perform(get("/stop")
                        .param("stopId", "999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void testGetAllStops() throws Exception {
        StopResponse stop1 = StopResponse.builder()
                .id(1L)
                .name("Test Stop 1")
                .latitude(new BigDecimal("40.7128"))
                .longitude(new BigDecimal("-74.0060"))
                .authorName("testUser1")
                .build();

        StopResponse stop2 = StopResponse.builder()
                .id(2L)
                .name("Test Stop 2")
                .latitude(new BigDecimal("40.7589"))
                .longitude(new BigDecimal("-73.9851"))
                .authorName("testUser2")
                .build();

        List<StopResponse> stops = Arrays.asList(stop1, stop2);
        PagedStopResponse pagedResponse = PagedStopResponse.builder()
                .stops(stops)
                .page(0)
                .size(10)
                .totalPages(1)
                .totalElements(2)
                .build();

        when(stopService.getAllStops(0, 10)).thenReturn(pagedResponse);

        mockMvc.perform(get("/stop/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stops").isArray())
                .andExpect(jsonPath("$.stops").value(org.hamcrest.Matchers.hasSize(2)))
                .andExpect(jsonPath("$.stops[0].id").value(1L))
                .andExpect(jsonPath("$.stops[0].name").value("Test Stop 1"))
                .andExpect(jsonPath("$.stops[1].id").value(2L))
                .andExpect(jsonPath("$.stops[1].name").value("Test Stop 2"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    @WithMockUser
    void testGetAllStopsWithCustomPagination() throws Exception {
        StopResponse stop = StopResponse.builder()
                .id(1L)
                .name("Test Stop")
                .latitude(new BigDecimal("40.7128"))
                .longitude(new BigDecimal("-74.0060"))
                .authorName("testUser")
                .build();

        List<StopResponse> stops = Arrays.asList(stop);
        PagedStopResponse pagedResponse = PagedStopResponse.builder()
                .stops(stops)
                .page(1)
                .size(5)
                .totalPages(2)
                .totalElements(10)
                .build();

        when(stopService.getAllStops(1, 5)).thenReturn(pagedResponse);

        mockMvc.perform(get("/stop/all")
                        .param("page", "1")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stops").isArray())
                .andExpect(jsonPath("$.stops").value(org.hamcrest.Matchers.hasSize(1)))
                .andExpect(jsonPath("$.stops[0].id").value(1L))
                .andExpect(jsonPath("$.stops[0].name").value("Test Stop"))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.totalElements").value(10));
    }

    @Test
    @WithMockUser
    void testGetAllStopsWithDefaultPagination() throws Exception {
        PagedStopResponse pagedResponse = PagedStopResponse.builder()
                .stops(Arrays.asList())
                .page(0)
                .size(10)
                .totalPages(0)
                .totalElements(0)
                .build();

        when(stopService.getAllStops(0, 10)).thenReturn(pagedResponse);

        mockMvc.perform(get("/stop/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stops").isArray())
                .andExpect(jsonPath("$.stops").value(org.hamcrest.Matchers.hasSize(0)))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalPages").value(0))
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void testCreateStopWithValidation() throws Exception {
        User user = User.builder()
                .id(1L)
                .name("testUser")
                .email("test@gmail.com")
                .build();

        StopCreateRequest request = new StopCreateRequest();
        request.setName("");
        request.setLatitude(new BigDecimal("40.7128"));
        request.setLongitude(new BigDecimal("-74.0060"));

        when(userDetailsService.loadUserByUsername("test@gmail.com")).thenReturn(user);

        mockMvc.perform(post("/stop/create")
                        .with(csrf())
                        .with(user(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}