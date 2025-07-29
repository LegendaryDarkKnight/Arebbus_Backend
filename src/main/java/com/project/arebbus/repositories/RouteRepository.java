package com.project.arebbus.repositories;

import com.project.arebbus.model.Route;
import com.project.arebbus.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface RouteRepository extends JpaRepository<Route, Long> {
    /**
     * Finds all Route entities by Author.
     * 
     * @param Author The Author to search for
     * @return List of Route entities matching the criteria
     */
    List<Route> findByAuthor(User author);
    /**
     * Finds all Route entities by NameContainingIgnoreCase.
     * 
     * @param NameContainingIgnoreCase The NameContainingIgnoreCase to search for
     * @return List of Route entities matching the criteria
     */
    List<Route> findByNameContainingIgnoreCase(String name);

    // Example: Find routes subscribed by a specific user (many-to-many query)
    /**
     * Custom query method with specific business logic.
     * 
     * @return Query result based on custom implementation
     */
    @Query("SELECT r FROM Route r JOIN r.subscriptions rs WHERE rs.user = :user")
    List<Route> findRoutesBySubscribedUser(@Param("user") User user);

    // Alternative approach using junction table
    /**
     * Custom query method with specific business logic.
     * 
     * @return Query result based on custom implementation
     */
    @Query("SELECT r FROM Route r WHERE r.id IN (SELECT rs.route.id FROM RouteSubscription rs WHERE rs.user.id = :userId)")
    List<Route> findRoutesByUserId(@Param("userId") Long userId);

    // Find routes with most subscribers
    /**
     * Custom query method with specific business logic.
     * 
     * @return Query result based on custom implementation
     */
    @Query("SELECT r FROM Route r LEFT JOIN r.subscriptions rs GROUP BY r ORDER BY COUNT(rs) DESC")
    List<Route> findRoutesBySubscriptionCountDesc();
}