package com.project.arebbus.exception;

public class BusNotFoundException extends RuntimeException {
    public BusNotFoundException(Long busId) {
        super("Bus with id " + busId + " not found");
    }
}