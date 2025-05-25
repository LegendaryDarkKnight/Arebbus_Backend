
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
    List<RouteSubscription> findByUser(User user);
    List<RouteSubscription> findByRoute(Route route);
    boolean existsByUserAndRoute(User user, Route route);

    // Count subscribers for a route
    @Query(nativeQuery = true,
            value =
            """
            SELECT COUNT(rs) FROM RouteSubscription rs
            WHERE rs.route = :route
            """
    )
    Long countSubscribersByRoute(@Param("route") Route route);

    // Find most popular routes by subscription count
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
