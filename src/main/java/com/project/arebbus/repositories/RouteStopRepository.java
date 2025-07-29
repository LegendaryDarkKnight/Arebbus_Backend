package com.project.arebbus.repositories;

import com.project.arebbus.model.RouteStop;
import com.project.arebbus.model.RouteStopId;
import com.project.arebbus.model.Route;
import com.project.arebbus.model.Stop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface RouteStopRepository extends JpaRepository<RouteStop, RouteStopId> {
    /**
     * Finds all RouteStop entities by Route.
     * 
     * @param Route The Route to search for
     * @return List of RouteStop entities matching the criteria
     */
    List<RouteStop> findByRoute(Route route);
    /**
     * Finds all RouteStop entities by Stop.
     * 
     * @param Stop The Stop to search for
     * @return List of RouteStop entities matching the criteria
     */
    List<RouteStop> findByStop(Stop stop);
    /**
     * Finds all RouteStop entities by RouteOrderByStopIndex.
     * 
     * @param RouteOrderByStopIndex The RouteOrderByStopIndex to search for
     * @return List of RouteStop entities matching the criteria
     */
    List<RouteStop> findByRouteOrderByStopIndex(Route route);

    // Find stops for a route in order
    /**
     * Custom query method with specific business logic.
     * 
     * @return Query result based on custom implementation
     */
    @Query("""
            SELECT rs.stop FROM RouteStop rs
            WHERE rs.route = :route
            ORDER BY rs.stopIndex
            """)
    List<Stop> findStopsForRouteInOrder(@Param("route") Route route);

    // Find routes that contain a specific stop
    /**
     * Custom query method with specific business logic.
     * 
     * @return Query result based on custom implementation
     */
     @Query("""
            SELECT rs.route FROM RouteStop rs
            WHERE rs.stop = :stop
            """)
    List<Route> findRoutesContainingStop(@Param("stop") Stop stop);
}