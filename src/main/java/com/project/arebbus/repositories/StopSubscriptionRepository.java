// StopSubscriptionRepository.java
package com.project.arebbus.repositories;

import com.project.arebbus.model.StopSubscription;
import com.project.arebbus.model.StopSubscriptionId;
import com.project.arebbus.model.Stop;
import com.project.arebbus.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StopSubscriptionRepository extends JpaRepository<StopSubscription, StopSubscriptionId> {
    /**
     * Finds all StopSubscription entities by User.
     * 
     * @param User The User to search for
     * @return List of StopSubscription entities matching the criteria
     */
    List<StopSubscription> findByUser(User user);
    /**
     * Finds all StopSubscription entities by Stop.
     * 
     * @param Stop The Stop to search for
     * @return List of StopSubscription entities matching the criteria
     */
    List<StopSubscription> findByStop(Stop stop);
    /**
     * Checks if an entity exists by UserAndStop.
     * 
     * @param UserAndStop The UserAndStop to check
     * @return true if entity exists, false otherwise
     */
    boolean existsByUserAndStop(User user, Stop stop);
}
