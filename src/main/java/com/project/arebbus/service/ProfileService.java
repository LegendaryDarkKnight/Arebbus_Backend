package com.project.arebbus.service;

import com.project.arebbus.dto.ProfileResponse;
import com.project.arebbus.exception.UserAlreadyExists;
import com.project.arebbus.exception.UserNotFound;
import com.project.arebbus.model.User;
import com.project.arebbus.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final UserRepository userRepository;
    public String getProfileName(String email){
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFound(email));

        return user.getName();
    }

    public ProfileResponse getProfile(String email){
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFound(email));
        return ProfileResponse.builder()
                .name(user.getName())
                .email(user.getEmail())
                .image(user.getImage())
                .reputation(user.getReputation())
                .build();
    }

}
