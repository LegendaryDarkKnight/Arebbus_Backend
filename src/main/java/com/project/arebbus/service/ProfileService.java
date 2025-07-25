package com.project.arebbus.service;

import com.project.arebbus.dto.*;
import com.project.arebbus.model.User;
import com.project.arebbus.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService {
  private final UserRepository userRepository;

  public ProfileResponse getProfile(User user) {
    return ProfileResponse.builder().name(user.getName()).email(user.getEmail()).build();
  }

  public ProfileResponse changeName(User user, String name) {
    user.setName(name);
    userRepository.save(user);
    return getProfile(user);
  }
}
