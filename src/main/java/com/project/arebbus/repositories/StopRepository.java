package com.project.arebbus.repositories;

import com.project.arebbus.model.Stop;
import com.project.arebbus.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.math.BigDecimal;

public interface StopRepository extends JpaRepository<Stop, Long> {
    List<Stop> findByAuthor(User author);
    List<Stop> findByNameContainingIgnoreCase(String name);

    // Find stops near a location
    @Query("""
            SELECT s FROM Stop s
            WHERE s.latitude BETWEEN :minLat AND :maxLat
            AND s.longitude BETWEEN :minLon AND :maxLon
            """)
    List<Stop> findStopsInArea(@Param("minLat") BigDecimal minLat, @Param("maxLat") BigDecimal maxLat,
                               @Param("minLon") BigDecimal minLon, @Param("maxLon") BigDecimal maxLon);

    // Find stops subscribed by a user
    @Query("""
            SELECT s FROM Stop s
            JOIN s.subscriptions ss
            WHERE ss.user = :user
            """)
    List<Stop> findStopsBySubscribedUser(@Param("user") User user);
}
