package com.project.arebbus.controller;

import com.project.arebbus.dto.BusLocationResponse;
import com.project.arebbus.dto.LocationResponse;
import com.project.arebbus.dto.LocationSetRequest;
import com.project.arebbus.dto.LocationUpdateRequest;
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
    public ResponseEntity<LocationResponse> setUserOnBus(@RequestBody LocationUpdateRequest request, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        LocationResponse response = locationService.setUserOnBus(user, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/user/no-track")
    public ResponseEntity<LocationResponse> setUserNoTrack(@RequestBody LocationUpdateRequest request, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        LocationResponse response = locationService.setUserNoTrack(user, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/get")
    public ResponseEntity<LocationResponse> getUserCurrentLocation(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        LocationResponse response = locationService.getUserCurrentLocation(user);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/bus/get")
    public ResponseEntity<BusLocationResponse> getBusLocations(@RequestParam Long busId) {
        BusLocationResponse response = locationService.getBusLocations(busId);
        return ResponseEntity.ok(response);
    }
}