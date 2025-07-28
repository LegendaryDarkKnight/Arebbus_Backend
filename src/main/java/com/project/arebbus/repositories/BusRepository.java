// BusRepository.java
package com.project.arebbus.repositories;

import com.project.arebbus.model.Bus;
import com.project.arebbus.model.Route;
import com.project.arebbus.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface BusRepository extends JpaRepository<Bus, Long> {
    List<Bus> findByAuthor(User author);
    List<Bus> findByRoute(Route route);
    List<Bus> findByStatus(String status);
    List<Bus> findByCapacityGreaterThan(Short capacity);

    // Find buses installed by a specific user (many-to-many through Install entity)
    @Query(nativeQuery = true, value="SELECT b FROM Bus b JOIN b.installs i WHERE i.user = :user")
    List<Bus> findBusesInstalledByUser(@Param("user") User user);

    // Alternative approach using Install table directly
    @Query("SELECT b FROM Bus b WHERE b.id IN (SELECT i.busId FROM Install i WHERE i.userId = :userId)")
    List<Bus> findBusesInstalledByUserId(@Param("userId") Long userId);

    // Find most popular buses (by install count)
    @Query("SELECT b FROM Bus b ORDER BY b.numInstall DESC")
    List<Bus> findBusesByInstallCountDesc();

    // Find buses with most upvotes
    @Query("SELECT b FROM Bus b ORDER BY b.numUpvote DESC")
    List<Bus> findBusesByUpvoteCountDesc();

    // Find users who installed a specific bus
    @Query("SELECT u FROM User u JOIN u.installations i WHERE i.bus = :bus")
    List<User> findUsersWhoInstalledBus(@Param("bus") Bus bus);
}
