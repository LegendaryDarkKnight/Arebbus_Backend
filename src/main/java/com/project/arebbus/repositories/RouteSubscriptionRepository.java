
package com.project.arebbus.repositories;

import com.project.arebbus.model.RouteSubscription;
import com.project.arebbus.model.RouteSubscriptionId;
import com.project.arebbus.model.Route;
import com.project.arebbus.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface RouteSubscriptionRepository extends JpaRepository<RouteSubscription, RouteSubscriptionId> {
    /**
     * Finds all RouteSubscription entities by User.
     * 
     * @param User The User to search for
     * @return List of RouteSubscription entities matching the criteria
     */
    List<RouteSubscription> findByUser(User user);
    /**
     * Finds all RouteSubscription entities by Route.
     * 
     * @param Route The Route to search for
     * @return List of RouteSubscription entities matching the criteria
     */
    List<RouteSubscription> findByRoute(Route route);
    /**
     * Checks if an entity exists by UserAndRoute.
     * 
     * @param UserAndRoute The UserAndRoute to check
     * @return true if entity exists, false otherwise
     */
    boolean existsByUserAndRoute(User user, Route route);

    // Count subscribers for a route
    /**
     * Custom query method with specific business logic.
     * 
     * @return Query result based on custom implementation
     */
    @Query(nativeQuery = true,
            value =
            """
            SELECT COUNT(rs) FROM RouteSubscription rs
            WHERE rs.route = :route
            """
    )
    Long countSubscribersByRoute(@Param("route") Route route);

    // Find most popular routes by subscription count
    /**
     * Custom query method with specific business logic.
     * 
     * @return Query result based on custom implementation
     */
    @Query(nativeQuery = true,
            value =
            """
            SELECT rs.route FROM RouteSubscription rs
            GROUP BY rs.route
            ORDER BY COUNT(rs) DESC
            """
    )
    List<Route> findMostPopularRoutes();
}
