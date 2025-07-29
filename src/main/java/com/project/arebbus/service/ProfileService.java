package com.project.arebbus.service;

import com.project.arebbus.dto.*;
import com.project.arebbus.model.User;
import com.project.arebbus.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class for handling user profile operations.
 * Provides functionality to retrieve and update user profile information.
 */
@Service
@RequiredArgsConstructor
public class ProfileService {
  
  /** Repository for user data access operations */
  /** Repository for  data access */
    private final UserRepository userRepository;

  /**
   * Retrieves the profile information for a given user.
   * 
   * @param user The user whose profile information is to be retrieved
   * @return ProfileResponse containing the user's name and email
   */
  public ProfileResponse getProfile(User user) {
    return ProfileResponse.builder().name(user.getName()).email(user.getEmail()).build();
  }

  /**
   * Updates the name of a user and saves the changes to the database.
   * 
   * @param user The user whose name is to be changed
   * @param name The new name to set for the user
   * @return ProfileResponse containing the updated user profile information
   */
  public ProfileResponse changeName(User user, String name) {
    // Update the user's name
    user.setName(name);
    
    // Persist the changes to the database
    userRepository.save(user);
    
    // Return the updated profile information
    return getProfile(user);
  }
}
