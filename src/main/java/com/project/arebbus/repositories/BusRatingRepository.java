package com.project.arebbus.repositories;

import com.project.arebbus.model.BusRating;
import com.project.arebbus.model.BusRatingId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BusRatingRepository extends JpaRepository<BusRating, BusRatingId> {
    boolean existsByUserIdAndBusId(Long userId, Long busId);
    Optional<BusRating> findByUserIdAndBusId(Long userId, Long busId);

    @Query(nativeQuery = true,value = "SELECT AVG(rating) FROM bus_rating where bus_id = :busId")
    double findAverageRating(@Param("busId") Long busId);
}
