package com.project.arebbus.controller;

import com.project.arebbus.dto.ProfileEditRequest;
import com.project.arebbus.dto.ProfileResponse;
import com.project.arebbus.model.User;
import com.project.arebbus.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileController {
  private final ProfileService profileService;

  @GetMapping
  public ResponseEntity<ProfileResponse> getProfile(Authentication authentication) {
    User user = (User) authentication.getPrincipal();
    return ResponseEntity.ok(profileService.getProfile(user));
  }

  @PostMapping("/edit")
  public ResponseEntity<ProfileResponse> editProfile(
      @RequestBody ProfileEditRequest request, Authentication authentication) {
    User user = (User) authentication.getPrincipal();
    return ResponseEntity.ok(profileService.changeName(user, request.getName()));
  }
}
