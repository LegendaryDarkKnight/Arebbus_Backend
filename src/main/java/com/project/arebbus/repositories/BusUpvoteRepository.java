package com.project.arebbus.repositories;

import com.project.arebbus.model.BusUpvote;
import com.project.arebbus.model.BusUpvoteId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusUpvoteRepository extends JpaRepository<BusUpvote, BusUpvoteId> {
    boolean existsByUserIdAndBusId(Long userId, Long busId);
}