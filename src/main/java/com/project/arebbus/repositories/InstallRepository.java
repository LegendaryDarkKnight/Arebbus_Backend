package com.project.arebbus.repositories;

import com.project.arebbus.model.Install;
import com.project.arebbus.model.InstallId;
import com.project.arebbus.model.Bus;
import com.project.arebbus.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface InstallRepository extends JpaRepository<Install, InstallId> {
    /**
     * Finds all Install entities by User.
     * 
     * @param User The User to search for
     * @return List of Install entities matching the criteria
     */
    List<Install> findByUser(User user);
    /**
     * Finds all Install entities by Bus.
     * 
     * @param Bus The Bus to search for
     * @return List of Install entities matching the criteria
     */
    List<Install> findByBus(Bus bus);
    /**
     * Checks if an entity exists by UserAndBus.
     * 
     * @param UserAndBus The UserAndBus to check
     * @return true if entity exists, false otherwise
     */
    boolean existsByUserAndBus(User user, Bus bus);

    // Count installations for a specific bus
    /**
     * Custom query method with specific business logic.
     * 
     * @return Query result based on custom implementation
     */
    @Query
    FROM Install i WHERE i.bus = :bus")
    Long countInstallationsByBus(@Param("bus") Bus bus);

    // Find most active users (by installation count)
    /**
     * Custom query method with specific business logic.
     * 
     * @return Query result based on custom implementation
     */
    @Query
    DESC")
    List<User> findMostActiveInstallers();
}
