package com.project.arebbus.repositories;

import com.project.arebbus.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.util.List;
import java.math.BigDecimal;

/**
 * Repository interface for User entity data access operations.
 * Extends JpaRepository to provide standard CRUD operations and custom query methods.
 */
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Finds a user by their email address.
     * 
     * @param email The email address to search for
     * @return Optional containing the User if found
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Checks if a user exists with the given email address.
     * 
     * @param email The email address to check
     * @return true if user exists, false otherwise
     */
    boolean existsByEmail(String email);
    
    /**
     * Finds all valid/active users in the system.
     * 
     * @return List of valid User entities
     */
    List<User> findByValidTrue();
    
    /**
     * Finds users with reputation greater than the specified threshold.
     * 
     * @param reputation The minimum reputation threshold
     * @return List of User entities with higher reputation
     */
    List<User> findByReputationGreaterThan(Integer reputation);

    /**
     * Finds users within a specified geographical area.
     * Uses native query to search for users between latitude and longitude bounds.
     * 
     * @param minLat Minimum latitude boundary
     * @param maxLat Maximum latitude boundary
     * @param minLon Minimum longitude boundary
     * @param maxLon Maximum longitude boundary
     * @return List of users within the specified area
     */
    @Query(nativeQuery = true,
            value =
            """
            SELECT u FROM users u
            WHERE u.latitude BETWEEN :minLat AND :maxLat
            AND u.longitude BETWEEN :minLon AND :maxLon
            """
    )
    List<User> findUsersInArea(@Param("minLat") BigDecimal minLat, @Param("maxLat") BigDecimal maxLat,
                               @Param("minLon") BigDecimal minLon, @Param("maxLon") BigDecimal maxLon);
}