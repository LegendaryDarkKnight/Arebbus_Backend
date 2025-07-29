package com.project.arebbus.repositories;

import com.project.arebbus.model.Location;
import com.project.arebbus.model.LocationId;
import com.project.arebbus.model.LocationStatus;
import com.project.arebbus.model.Bus;
import com.project.arebbus.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface LocationRepository extends JpaRepository<Location, LocationId> {
    /**
     * Finds all Location entities by Bus.
     * 
     * @param Bus The Bus to search for
     * @return List of Location entities matching the criteria
     */
    List<Location> findByBus(Bus bus);
    /**
     * Finds all Location entities by BusId.
     * 
     * @param BusId The BusId to search for
     * @return List of Location entities matching the criteria
     */
    List<Location> findByBusId(Long busId);
    /**
     * Finds all Location entities by User.
     * 
     * @param User The User to search for
     * @return List of Location entities matching the criteria
     */
    List<Location> findByUser(User user);
    /**
     * Finds all Location entities by Status.
     * 
     * @param Status The Status to search for
     * @return List of Location entities matching the criteria
     */
    List<Location> findByStatus(LocationStatus status);
    /**
     * Finds all Location entities by TimeBetween.
     * 
     * @param TimeBetween The TimeBetween to search for
     * @return List of Location entities matching the criteria
     */
    List<Location> findByTimeBetween(LocalDateTime start, LocalDateTime end);

    // Find latest location for each bus
    /**
     * Custom query method with specific business logic.
     * 
     * @return Query result based on custom implementation
     */
    @Query
    FROM Location l2 WHERE l2.bus = l.bus)")
    List<Location> findLatestLocationForEachBus();

    // Find users currently on a specific bus
    /**
     * Custom query method with specific business logic.
     * 
     * @return Query result based on custom implementation
     */
    @Query
    FROM Location l2 WHERE l2.user = l.user)")
    List<User> findUsersCurrentlyOnBus(@Param("bus") Bus bus);
}