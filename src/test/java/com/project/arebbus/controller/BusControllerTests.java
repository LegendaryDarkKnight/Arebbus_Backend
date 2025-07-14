package com.project.arebbus.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.arebbus.dto.BusCreateRequest;
import com.project.arebbus.dto.BusInstallRequest;
import com.project.arebbus.dto.BusInstallResponse;
import com.project.arebbus.dto.BusResponse;
import com.project.arebbus.dto.PagedBusResponse;
import com.project.arebbus.exception.BusNotFoundException;
import com.project.arebbus.model.User;
import com.project.arebbus.service.BusService;
import com.project.arebbus.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BusController.class)
class BusControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BusService busService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "test@gmail.com")
    void testCreateBus() throws Exception {
        User user = User.builder()
                .id(1L)
                .name("testUser")
                .email("test@gmail.com")
                .build();

        BusCreateRequest request = new BusCreateRequest();
        request.setName("Test Bus");
        request.setRouteId(1L);
        request.setCapacity((short) 50);

        BusResponse response = BusResponse.builder()
                .id(1L)
                .name("Test Bus")
                .authorName("testUser")
                .capacity((short) 50)
                .numInstall(0)
                .numUpvote(0L)
                .upvoted(false)
                .build();

        when(userDetailsService.loadUserByUsername("test@gmail.com")).thenReturn(user);
        when(busService.createBus(any(User.class), any(BusCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/bus/create")
                        .with(csrf())
                        .with(user(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Bus"))
                .andExpect(jsonPath("$.authorName").value("testUser"))
                .andExpect(jsonPath("$.capacity").value(50))
                .andExpect(jsonPath("$.numInstall").value(0))
                .andExpect(jsonPath("$.numUpvote").value(0))
                .andExpect(jsonPath("$.upvoted").value(false));
    }

    @Test
    @WithMockUser
    void testGetBusById() throws Exception {
        User user = User.builder()
                .id(1L)
                .name("testUser")
                .email("test@gmail.com")
                .build();

        BusResponse response = BusResponse.builder()
                .id(1L)
                .name("Test Bus")
                .authorName("testUser")
                .capacity((short) 50)
                .numInstall(10)
                .numUpvote(5L)
                .upvoted(true)
                .build();

        when(userDetailsService.loadUserByUsername("user")).thenReturn(user);
        when(busService.getBusById(eq(1L), any(User.class))).thenReturn(response);

        mockMvc.perform(get("/bus")
                        .param("busId", "1")
                        .with(user(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Bus"))
                .andExpect(jsonPath("$.authorName").value("testUser"))
                .andExpect(jsonPath("$.capacity").value(50))
                .andExpect(jsonPath("$.numInstall").value(10))
                .andExpect(jsonPath("$.numUpvote").value(5))
                .andExpect(jsonPath("$.upvoted").value(true));
    }

    @Test
    @WithMockUser
    void testGetBusByIdNotFound() throws Exception {
        User user = User.builder()
                .id(1L)
                .name("testUser")
                .email("test@gmail.com")
                .build();

        when(userDetailsService.loadUserByUsername("user")).thenReturn(user);
        when(busService.getBusById(eq(999L), any(User.class))).thenThrow(new BusNotFoundException(999L));

        mockMvc.perform(get("/bus")
                        .param("busId", "999")
                        .with(user(user)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void testGetAllBuses() throws Exception {
        User user = User.builder()
                .id(1L)
                .name("testUser")
                .email("test@gmail.com")
                .build();

        BusResponse bus1 = BusResponse.builder()
                .id(1L)
                .name("Test Bus 1")
                .authorName("testUser1")
                .capacity((short) 50)
                .numInstall(0)
                .numUpvote(0L)
                .upvoted(false)
                .build();

        BusResponse bus2 = BusResponse.builder()
                .id(2L)
                .name("Test Bus 2")
                .authorName("testUser2")
                .capacity((short) 60)
                .numInstall(0)
                .numUpvote(0L)
                .upvoted(false)
                .build();

        List<BusResponse> buses = Arrays.asList(bus1, bus2);
        PagedBusResponse pagedResponse = PagedBusResponse.builder()
                .buses(buses)
                .page(0)
                .size(10)
                .totalPages(1)
                .totalElements(2)
                .build();

        when(userDetailsService.loadUserByUsername("user")).thenReturn(user);
        when(busService.getAllBuses(any(User.class), eq(0), eq(10))).thenReturn(pagedResponse);

        mockMvc.perform(get("/bus/all")
                        .with(user(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.buses").isArray())
                .andExpect(jsonPath("$.buses").value(org.hamcrest.Matchers.hasSize(2)))
                .andExpect(jsonPath("$.buses[0].id").value(1L))
                .andExpect(jsonPath("$.buses[0].name").value("Test Bus 1"))
                .andExpect(jsonPath("$.buses[1].id").value(2L))
                .andExpect(jsonPath("$.buses[1].name").value("Test Bus 2"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    @WithMockUser
    void testGetAllBusesWithCustomPagination() throws Exception {
        User user = User.builder()
                .id(1L)
                .name("testUser")
                .email("test@gmail.com")
                .build();

        BusResponse bus = BusResponse.builder()
                .id(1L)
                .name("Test Bus")
                .authorName("testUser")
                .capacity((short) 50)
                .numInstall(0)
                .numUpvote(0L)
                .upvoted(false)
                .build();

        List<BusResponse> buses = Arrays.asList(bus);
        PagedBusResponse pagedResponse = PagedBusResponse.builder()
                .buses(buses)
                .page(1)
                .size(5)
                .totalPages(2)
                .totalElements(10)
                .build();

        when(userDetailsService.loadUserByUsername("user")).thenReturn(user);
        when(busService.getAllBuses(any(User.class), eq(1), eq(5))).thenReturn(pagedResponse);

        mockMvc.perform(get("/bus/all")
                        .param("page", "1")
                        .param("size", "5")
                        .with(user(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.buses").isArray())
                .andExpect(jsonPath("$.buses").value(org.hamcrest.Matchers.hasSize(1)))
                .andExpect(jsonPath("$.buses[0].id").value(1L))
                .andExpect(jsonPath("$.buses[0].name").value("Test Bus"))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.totalElements").value(10));
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void testInstallBus() throws Exception {
        User user = User.builder()
                .id(1L)
                .name("testUser")
                .email("test@gmail.com")
                .build();

        BusInstallRequest request = new BusInstallRequest();
        request.setBusId(1L);

        BusInstallResponse response = BusInstallResponse.builder()
                .busId(1L)
                .busName("Test Bus")
                .message("Bus installed successfully")
                .installed(true)
                .build();

        when(userDetailsService.loadUserByUsername("test@gmail.com")).thenReturn(user);
        when(busService.installBus(any(User.class), any(BusInstallRequest.class))).thenReturn(response);

        mockMvc.perform(post("/bus/install")
                        .with(csrf())
                        .with(user(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.busId").value(1L))
                .andExpect(jsonPath("$.busName").value("Test Bus"))
                .andExpect(jsonPath("$.message").value("Bus installed successfully"))
                .andExpect(jsonPath("$.installed").value(true));
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void testUninstallBus() throws Exception {
        User user = User.builder()
                .id(1L)
                .name("testUser")
                .email("test@gmail.com")
                .build();

        BusInstallRequest request = new BusInstallRequest();
        request.setBusId(1L);

        BusInstallResponse response = BusInstallResponse.builder()
                .busId(1L)
                .busName("Test Bus")
                .message("Bus uninstalled successfully")
                .installed(false)
                .build();

        when(userDetailsService.loadUserByUsername("test@gmail.com")).thenReturn(user);
        when(busService.uninstallBus(any(User.class), any(BusInstallRequest.class))).thenReturn(response);

        mockMvc.perform(post("/bus/uninstall")
                        .with(csrf())
                        .with(user(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.busId").value(1L))
                .andExpect(jsonPath("$.busName").value("Test Bus"))
                .andExpect(jsonPath("$.message").value("Bus uninstalled successfully"))
                .andExpect(jsonPath("$.installed").value(false));
    }

    @Test
    @WithMockUser
    void testGetInstalledBuses() throws Exception {
        User user = User.builder()
                .id(1L)
                .name("testUser")
                .email("test@gmail.com")
                .build();

        BusResponse bus1 = BusResponse.builder()
                .id(1L)
                .name("Installed Bus 1")
                .authorName("testUser1")
                .capacity((short) 50)
                .numInstall(0)
                .numUpvote(0L)
                .upvoted(false)
                .build();

        BusResponse bus2 = BusResponse.builder()
                .id(2L)
                .name("Installed Bus 2")
                .authorName("testUser2")
                .capacity((short) 60)
                .numInstall(0)
                .numUpvote(0L)
                .upvoted(false)
                .build();

        List<BusResponse> buses = Arrays.asList(bus1, bus2);
        PagedBusResponse pagedResponse = PagedBusResponse.builder()
                .buses(buses)
                .page(0)
                .size(10)
                .totalPages(1)
                .totalElements(2)
                .build();

        when(userDetailsService.loadUserByUsername("user")).thenReturn(user);
        when(busService.getInstalledBuses(any(User.class), eq(0), eq(10))).thenReturn(pagedResponse);

        mockMvc.perform(get("/bus/installed")
                        .with(user(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.buses").isArray())
                .andExpect(jsonPath("$.buses").value(org.hamcrest.Matchers.hasSize(2)))
                .andExpect(jsonPath("$.buses[0].id").value(1L))
                .andExpect(jsonPath("$.buses[0].name").value("Installed Bus 1"))
                .andExpect(jsonPath("$.buses[1].id").value(2L))
                .andExpect(jsonPath("$.buses[1].name").value("Installed Bus 2"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    @WithMockUser
    void testGetInstalledBusesWithCustomPagination() throws Exception {
        User user = User.builder()
                .id(1L)
                .name("testUser")
                .email("test@gmail.com")
                .build();

        BusResponse bus = BusResponse.builder()
                .id(1L)
                .name("Installed Bus")
                .authorName("testUser")
                .capacity((short) 50)
                .numInstall(0)
                .numUpvote(0L)
                .upvoted(false)
                .build();

        List<BusResponse> buses = Arrays.asList(bus);
        PagedBusResponse pagedResponse = PagedBusResponse.builder()
                .buses(buses)
                .page(1)
                .size(5)
                .totalPages(3)
                .totalElements(15)
                .build();

        when(userDetailsService.loadUserByUsername("user")).thenReturn(user);
        when(busService.getInstalledBuses(any(User.class), eq(1), eq(5))).thenReturn(pagedResponse);

        mockMvc.perform(get("/bus/installed")
                        .param("page", "1")
                        .param("size", "5")
                        .with(user(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.buses").isArray())
                .andExpect(jsonPath("$.buses").value(org.hamcrest.Matchers.hasSize(1)))
                .andExpect(jsonPath("$.buses[0].id").value(1L))
                .andExpect(jsonPath("$.buses[0].name").value("Installed Bus"))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.totalElements").value(15));
    }
}