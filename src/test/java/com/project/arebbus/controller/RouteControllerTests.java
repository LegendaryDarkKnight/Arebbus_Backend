package com.project.arebbus.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.arebbus.dto.PagedRouteResponse;
import com.project.arebbus.dto.RouteCreateRequest;
import com.project.arebbus.dto.RouteResponse;
import com.project.arebbus.dto.StopResponse;
import com.project.arebbus.exception.RouteNotFoundException;
import com.project.arebbus.model.User;
import com.project.arebbus.service.JwtService;
import com.project.arebbus.service.RouteService;
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

@WebMvcTest(RouteController.class)
class RouteControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RouteService routeService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "test@gmail.com")
    void testCreateRoute() throws Exception {
        User user = User.builder()
                .id(1L)
                .name("testUser")
                .email("test@gmail.com")
                .build();

        RouteCreateRequest request = new RouteCreateRequest();
        request.setName("Test Route");
        request.setStopIds(Arrays.asList(1L, 2L));

        StopResponse stop1 = StopResponse.builder()
                .id(1L)
                .name("Stop 1")
                .latitude(new BigDecimal("40.7128"))
                .longitude(new BigDecimal("-74.0060"))
                .authorName("testUser")
                .build();

        StopResponse stop2 = StopResponse.builder()
                .id(2L)
                .name("Stop 2")
                .latitude(new BigDecimal("40.7589"))
                .longitude(new BigDecimal("-73.9851"))
                .authorName("testUser")
                .build();

        RouteResponse response = RouteResponse.builder()
                .id(1L)
                .name("Test Route")
                .authorName("testUser")
                .stops(Arrays.asList(stop1, stop2))
                .build();

        when(userDetailsService.loadUserByUsername("test@gmail.com")).thenReturn(user);
        when(routeService.createRoute(any(User.class), any(RouteCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/route/create")
                        .with(csrf())
                        .with(user(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Route"))
                .andExpect(jsonPath("$.authorName").value("testUser"))
                .andExpect(jsonPath("$.stops").isArray())
                .andExpect(jsonPath("$.stops").value(org.hamcrest.Matchers.hasSize(2)))
                .andExpect(jsonPath("$.stops[0].id").value(1L))
                .andExpect(jsonPath("$.stops[0].name").value("Stop 1"))
                .andExpect(jsonPath("$.stops[1].id").value(2L))
                .andExpect(jsonPath("$.stops[1].name").value("Stop 2"));
    }

    @Test
    @WithMockUser
    void testGetRouteById() throws Exception {
        StopResponse stop = StopResponse.builder()
                .id(1L)
                .name("Test Stop")
                .latitude(new BigDecimal("40.7128"))
                .longitude(new BigDecimal("-74.0060"))
                .authorName("testUser")
                .build();

        RouteResponse response = RouteResponse.builder()
                .id(1L)
                .name("Test Route")
                .authorName("testUser")
                .stops(Arrays.asList(stop))
                .build();

        when(routeService.getRouteById(1L)).thenReturn(response);

        mockMvc.perform(get("/route")
                        .param("routeId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Route"))
                .andExpect(jsonPath("$.authorName").value("testUser"))
                .andExpect(jsonPath("$.stops").isArray())
                .andExpect(jsonPath("$.stops").value(org.hamcrest.Matchers.hasSize(1)))
                .andExpect(jsonPath("$.stops[0].name").value("Test Stop"));
    }

    @Test
    @WithMockUser
    void testGetRouteByIdNotFound() throws Exception {
        when(routeService.getRouteById(999L)).thenThrow(new RouteNotFoundException(999L));

        mockMvc.perform(get("/route")
                        .param("routeId", "999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void testGetAllRoutes() throws Exception {
        StopResponse stop1 = StopResponse.builder()
                .id(1L)
                .name("Stop 1")
                .latitude(new BigDecimal("40.7128"))
                .longitude(new BigDecimal("-74.0060"))
                .authorName("testUser1")
                .build();

        RouteResponse route1 = RouteResponse.builder()
                .id(1L)
                .name("Test Route 1")
                .authorName("testUser1")
                .stops(Arrays.asList(stop1))
                .build();

        RouteResponse route2 = RouteResponse.builder()
                .id(2L)
                .name("Test Route 2")
                .authorName("testUser2")
                .stops(Arrays.asList())
                .build();

        List<RouteResponse> routes = Arrays.asList(route1, route2);
        PagedRouteResponse pagedResponse = PagedRouteResponse.builder()
                .routes(routes)
                .page(0)
                .size(10)
                .totalPages(1)
                .totalElements(2)
                .build();

        when(routeService.getAllRoutes(0, 10)).thenReturn(pagedResponse);

        mockMvc.perform(get("/route/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.routes").isArray())
                .andExpect(jsonPath("$.routes").value(org.hamcrest.Matchers.hasSize(2)))
                .andExpect(jsonPath("$.routes[0].id").value(1L))
                .andExpect(jsonPath("$.routes[0].name").value("Test Route 1"))
                .andExpect(jsonPath("$.routes[1].id").value(2L))
                .andExpect(jsonPath("$.routes[1].name").value("Test Route 2"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    @WithMockUser
    void testGetAllRoutesWithCustomPagination() throws Exception {
        RouteResponse route = RouteResponse.builder()
                .id(1L)
                .name("Test Route")
                .authorName("testUser")
                .stops(Arrays.asList())
                .build();

        List<RouteResponse> routes = Arrays.asList(route);
        PagedRouteResponse pagedResponse = PagedRouteResponse.builder()
                .routes(routes)
                .page(1)
                .size(5)
                .totalPages(2)
                .totalElements(10)
                .build();

        when(routeService.getAllRoutes(1, 5)).thenReturn(pagedResponse);

        mockMvc.perform(get("/route/all")
                        .param("page", "1")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.routes").isArray())
                .andExpect(jsonPath("$.routes").value(org.hamcrest.Matchers.hasSize(1)))
                .andExpect(jsonPath("$.routes[0].id").value(1L))
                .andExpect(jsonPath("$.routes[0].name").value("Test Route"))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.totalElements").value(10));
    }

    @Test
    @WithMockUser
    void testGetAllRoutesWithDefaultPagination() throws Exception {
        PagedRouteResponse pagedResponse = PagedRouteResponse.builder()
                .routes(Arrays.asList())
                .page(0)
                .size(10)
                .totalPages(0)
                .totalElements(0)
                .build();

        when(routeService.getAllRoutes(0, 10)).thenReturn(pagedResponse);

        mockMvc.perform(get("/route/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.routes").isArray())
                .andExpect(jsonPath("$.routes").value(org.hamcrest.Matchers.hasSize(0)))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalPages").value(0))
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void testCreateRouteWithEmptyStops() throws Exception {
        User user = User.builder()
                .id(1L)
                .name("testUser")
                .email("test@gmail.com")
                .build();

        RouteCreateRequest request = new RouteCreateRequest();
        request.setName("Empty Route");
        request.setStopIds(Arrays.asList());

        RouteResponse response = RouteResponse.builder()
                .id(1L)
                .name("Empty Route")
                .authorName("testUser")
                .stops(Arrays.asList())
                .build();

        when(userDetailsService.loadUserByUsername("test@gmail.com")).thenReturn(user);
        when(routeService.createRoute(any(User.class), any(RouteCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/route/create")
                        .with(csrf())
                        .with(user(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Empty Route"))
                .andExpect(jsonPath("$.stops").isArray())
                .andExpect(jsonPath("$.stops").value(org.hamcrest.Matchers.hasSize(0)));
    }
}