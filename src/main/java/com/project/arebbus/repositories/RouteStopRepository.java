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
    List<RouteStop> findByRoute(Route route);
    List<RouteStop> findByStop(Stop stop);
    List<RouteStop> findByRouteOrderByStopIndex(Route route);

    // Find stops for a route in order
    @Query("""
            SELECT rs.stop FROM RouteStop rs
            WHERE rs.route = :route
            ORDER BY rs.stopIndex
            """)
    List<Stop> findStopsForRouteInOrder(@Param("route") Route route);

    // Find routes that contain a specific stop
    @Query("""
            SELECT rs.route FROM RouteStop rs
            WHERE rs.stop = :stop
            """)
    List<Route> findRoutesContainingStop(@Param("stop") Stop stop);
}