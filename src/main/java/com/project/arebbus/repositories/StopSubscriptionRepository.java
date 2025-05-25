// StopSubscriptionRepository.java
package com.project.arebbus.repositories;

import com.project.arebbus.model.StopSubscription;
import com.project.arebbus.model.StopSubscriptionId;
import com.project.arebbus.model.Stop;
import com.project.arebbus.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StopSubscriptionRepository extends JpaRepository<StopSubscription, StopSubscriptionId> {
    List<StopSubscription> findByUser(User user);
    List<StopSubscription> findByStop(Stop stop);
    boolean existsByUserAndStop(User user, Stop stop);
}
