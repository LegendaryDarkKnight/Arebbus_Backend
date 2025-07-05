package com.project.arebbus.exception;

public class RouteNotFoundException extends RuntimeException {
    public RouteNotFoundException(Long routeId) {
        super("Route with id " + routeId + " not found");
    }
}