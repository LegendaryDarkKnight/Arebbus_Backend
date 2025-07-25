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
    List<Location> findByBus(Bus bus);
    List<Location> findByBusId(Long busId);
    List<Location> findByUser(User user);
    List<Location> findByStatus(LocationStatus status);
    List<Location> findByTimeBetween(LocalDateTime start, LocalDateTime end);

    // Find latest location for each bus
    @Query(nativeQuery = true,value = "SELECT l FROM Location l WHERE l.time = (SELECT MAX(l2.time) FROM Location l2 WHERE l2.bus = l.bus)")
    List<Location> findLatestLocationForEachBus();

    // Find users currently on a specific bus
    @Query(nativeQuery = true,value = "SELECT l.user FROM Location l WHERE l.bus = :bus AND l.status = 'ON_BUS' AND l.time = (SELECT MAX(l2.time) FROM Location l2 WHERE l2.user = l.user)")
    List<User> findUsersCurrentlyOnBus(@Param("bus") Bus bus);
}