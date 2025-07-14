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
    List<Install> findByUser(User user);
    List<Install> findByBus(Bus bus);
    boolean existsByUserAndBus(User user, Bus bus);

    // Count installations for a specific bus
    @Query("SELECT COUNT(i) FROM Install i WHERE i.bus = :bus")
    Long countInstallationsByBus(@Param("bus") Bus bus);

    // Find most active users (by installation count)
    @Query("SELECT i.user FROM Install i GROUP BY i.user ORDER BY COUNT(i) DESC")
    List<User> findMostActiveInstallers();
}
