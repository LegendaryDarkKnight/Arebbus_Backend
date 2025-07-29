package com.project.arebbus.repositories;

import com.project.arebbus.model.BusUpvote;
import com.project.arebbus.model.BusUpvoteId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusUpvoteRepository extends JpaRepository<BusUpvote, BusUpvoteId> {
    /**
     * Checks if an entity exists by UserIdAndBusId.
     * 
     * @param UserIdAndBusId The UserIdAndBusId to check
     * @return true if entity exists, false otherwise
     */
    boolean existsByUserIdAndBusId(Long userId, Long busId);
}