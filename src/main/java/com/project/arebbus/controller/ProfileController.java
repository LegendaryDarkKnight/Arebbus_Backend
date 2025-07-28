package com.project.arebbus.controller;
import com.project.arebbus.dto.ProfileResponse;
import com.project.arebbus.model.User;
import com.project.arebbus.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")

public class ProfileController {
    private final ProfileService profileService;

    @GetMapping("/name")
    public ResponseEntity<String> getProfileName(Authentication authentication){
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(profileService.getProfileName(user.getEmail()));
    }

    @GetMapping("")
    public ResponseEntity<ProfileResponse> getUser(Authentication authentication){
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(profileService.getProfile(user.getEmail()));
    }
}
