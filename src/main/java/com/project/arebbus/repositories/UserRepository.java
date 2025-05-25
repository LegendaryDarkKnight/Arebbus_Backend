package com.project.arebbus.repositories;

import com.project.arebbus.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.util.List;
import java.math.BigDecimal;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findByValidTrue();
    List<User> findByReputationGreaterThan(Integer reputation);

    // Example: Find users within a certain distance (approximate)
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