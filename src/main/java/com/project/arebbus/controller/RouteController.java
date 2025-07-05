package com.project.arebbus.controller;

import com.project.arebbus.dto.PagedRouteResponse;
import com.project.arebbus.dto.RouteCreateRequest;
import com.project.arebbus.dto.RouteResponse;
import com.project.arebbus.model.User;
import com.project.arebbus.service.RouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/route")
public class RouteController {

    private final RouteService routeService;

    @PostMapping("/create")
    public ResponseEntity<RouteResponse> createRoute(@RequestBody RouteCreateRequest request, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(routeService.createRoute(user, request));
    }

    @GetMapping
    public ResponseEntity<RouteResponse> getRouteById(@RequestParam Long routeId) {
        return ResponseEntity.ok(routeService.getRouteById(routeId));
    }

    @GetMapping("/all")
    public ResponseEntity<PagedRouteResponse> getAllRoutes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(routeService.getAllRoutes(page, size));
    }
}