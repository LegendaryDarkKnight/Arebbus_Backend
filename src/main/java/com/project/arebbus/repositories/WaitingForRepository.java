package com.project.arebbus.repositories;

import com.project.arebbus.model.WaitingFor;
import com.project.arebbus.model.WaitingForId;
import com.project.arebbus.model.Bus;
import com.project.arebbus.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WaitingForRepository extends JpaRepository<WaitingFor, WaitingForId> {
    /**
     * Finds all WaitingFor entities by User.
     * 
     * @param User The User to search for
     * @return List of WaitingFor entities matching the criteria
     */
    List<WaitingFor> findByUser(User user);
    /**
     * Finds all WaitingFor entities by Bus.
     * 
     * @param Bus The Bus to search for
     * @return List of WaitingFor entities matching the criteria
     */
    List<WaitingFor> findByBus(Bus bus);
    /**
     * Finds all WaitingFor entities by ValidTrue.
     * 
     * @param ValidTrue The ValidTrue to search for
     * @return List of WaitingFor entities matching the criteria
     */
    List<WaitingFor> findByValidTrue();
    /**
     * Finds all WaitingFor entities by BusAndValidTrue.
     * 
     * @param BusAndValidTrue The BusAndValidTrue to search for
     * @return List of WaitingFor entities matching the criteria
     */
    List<WaitingFor> findByBusAndValidTrue(Bus bus);
    /**
     * Checks if an entity exists by UserAndBusAndValidTrue.
     * 
     * @param UserAndBusAndValidTrue The UserAndBusAndValidTrue to check
     * @return true if entity exists, false otherwise
     */
    boolean existsByUserAndBusAndValidTrue(User user, Bus bus);
}