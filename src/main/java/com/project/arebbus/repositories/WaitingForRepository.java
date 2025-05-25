package com.project.arebbus.repositories;

import com.project.arebbus.model.WaitingFor;
import com.project.arebbus.model.WaitingForId;
import com.project.arebbus.model.Bus;
import com.project.arebbus.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WaitingForRepository extends JpaRepository<WaitingFor, WaitingForId> {
    List<WaitingFor> findByUser(User user);
    List<WaitingFor> findByBus(Bus bus);
    List<WaitingFor> findByValidTrue();
    List<WaitingFor> findByBusAndValidTrue(Bus bus);
    boolean existsByUserAndBusAndValidTrue(User user, Bus bus);
}