package com.project.arebbus.exception;

import com.project.arebbus.model.LocationStatus;

public class InvalidLocationStatusTransitionException extends RuntimeException {
    public InvalidLocationStatusTransitionException(LocationStatus from, LocationStatus to) {
        super("Invalid status transition from " + from + " to " + to);
    }
    
    public InvalidLocationStatusTransitionException(String message) {
        super(message);
    }
}