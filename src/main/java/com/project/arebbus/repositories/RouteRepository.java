package com.project.arebbus.repositories;

import com.project.arebbus.model.Route;
import com.project.arebbus.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface RouteRepository extends JpaRepository<Route, Long> {
    List<Route> findByAuthor(User author);
    List<Route> findByNameContainingIgnoreCase(String name);

    // Example: Find routes subscribed by a specific user (many-to-many query)
    @Query(nativeQuery = true,value = "SELECT r FROM Route r JOIN r.subscriptions rs WHERE rs.user = :user")
    List<Route> findRoutesBySubscribedUser(@Param("user") User user);

    // Alternative approach using junction table
    @Query(nativeQuery = true, value = "SELECT r FROM Route r WHERE r.id IN (SELECT rs.routeId FROM RouteSubscription rs WHERE rs.userId = :userId)")
    List<Route> findRoutesByUserId(@Param("userId") Long userId);

    // Find routes with most subscribers
    @Query(nativeQuery = true,value = "SELECT r FROM Route r LEFT JOIN r.subscriptions rs GROUP BY r ORDER BY COUNT(rs) DESC")
    List<Route> findRoutesBySubscriptionCountDesc();
}