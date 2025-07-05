package com.project.arebbus.controller;

import com.project.arebbus.dto.BusCreateRequest;
import com.project.arebbus.dto.BusResponse;
import com.project.arebbus.dto.PagedBusResponse;
import com.project.arebbus.model.User;
import com.project.arebbus.service.BusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bus")
public class BusController {

    private final BusService busService;

    @PostMapping("/create")
    public ResponseEntity<BusResponse> createBus(@RequestBody BusCreateRequest request, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(busService.createBus(user, request));
    }

    @GetMapping
    public ResponseEntity<BusResponse> getBusById(@RequestParam Long busId, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(busService.getBusById(busId, user));
    }

    @GetMapping("/all")
    public ResponseEntity<PagedBusResponse> getAllBuses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(busService.getAllBuses(user, page, size));
    }
}