package com.project.arebbus.controller;

import com.project.arebbus.dto.LocationResponse;
import com.project.arebbus.dto.LocationSetRequest;
import com.project.arebbus.model.User;
import com.project.arebbus.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/location")
public class LocationController {

    private final LocationService locationService;

    @PostMapping("/user/waiting")
    public ResponseEntity<LocationResponse> setUserWaiting(@RequestBody LocationSetRequest request, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        LocationResponse response = locationService.setUserWaiting(user, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/user/on-bus")
    public ResponseEntity<LocationResponse> setUserOnBus(@RequestBody LocationSetRequest request, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        LocationResponse response = locationService.setUserOnBus(user, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/user/left-bus")
    public ResponseEntity<LocationResponse> setUserLeftBus(@RequestBody LocationSetRequest request, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        LocationResponse response = locationService.setUserLeftBus(user, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/get")
    public ResponseEntity<LocationResponse> getUserCurrentLocation(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        LocationResponse response = locationService.getUserCurrentLocation(user);
        return ResponseEntity.ok(response);
    }
}