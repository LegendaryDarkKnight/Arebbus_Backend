package com.project.arebbus.repositories;

import com.project.arebbus.model.Stop;
import com.project.arebbus.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.math.BigDecimal;

public interface StopRepository extends JpaRepository<Stop, Long> {
    /**
     * Finds all Stop entities by Author.
     * 
     * @param Author The Author to search for
     * @return List of Stop entities matching the criteria
     */
    List<Stop> findByAuthor(User author);
    /**
     * Finds all Stop entities by NameContainingIgnoreCase.
     * 
     * @param NameContainingIgnoreCase The NameContainingIgnoreCase to search for
     * @return List of Stop entities matching the criteria
     */
    List<Stop> findByNameContainingIgnoreCase(String name);

    // Find stops near a location
    /**
     * Custom query method with specific business logic.
     * 
     * @return Query result based on custom implementation
     */
    @Query("""
            SELECT s FROM Stop s
            WHERE s.latitude BETWEEN :minLat AND :maxLat
            AND s.longitude BETWEEN :minLon AND :maxLon
            """)
    List<Stop> findStopsInArea(@Param("minLat") BigDecimal minLat, @Param("maxLat") BigDecimal maxLat,
                               @Param("minLon") BigDecimal minLon, @Param("maxLon") BigDecimal maxLon);

    // Find stops subscribed by a user
    /**
     * Custom query method with specific business logic.
     * 
     * @return Query result based on custom implementation
     */
    @Query("""
            SELECT s FROM Stop s
            JOIN s.subscriptions ss
            WHERE ss.user = :user
            """)
    List<Stop> findStopsBySubscribedUser(@Param("user") User user);
}
