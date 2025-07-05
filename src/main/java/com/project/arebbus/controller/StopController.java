package com.project.arebbus.controller;

import com.project.arebbus.dto.PagedStopResponse;
import com.project.arebbus.dto.StopCreateRequest;
import com.project.arebbus.dto.StopResponse;
import com.project.arebbus.model.User;
import com.project.arebbus.service.StopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stop")
public class StopController {

    private final StopService stopService;

    @PostMapping("/create")
    public ResponseEntity<StopResponse> createStop(@RequestBody StopCreateRequest request, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(stopService.createStop(user, request));
    }

    @GetMapping
    public ResponseEntity<StopResponse> getStopById(@RequestParam Long stopId) {
        return ResponseEntity.ok(stopService.getStopById(stopId));
    }

    @GetMapping("/all")
    public ResponseEntity<PagedStopResponse> getAllStops(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(stopService.getAllStops(page, size));
    }
}