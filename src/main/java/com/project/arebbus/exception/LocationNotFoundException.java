package com.project.arebbus.exception;

public class LocationNotFoundException extends RuntimeException {
    public LocationNotFoundException() {
        super("No location found for user");
    }
    
    public LocationNotFoundException(Long userId) {
        super("No location found for user with id " + userId);
    }
}