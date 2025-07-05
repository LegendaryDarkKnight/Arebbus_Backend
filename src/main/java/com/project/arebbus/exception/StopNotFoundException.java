package com.project.arebbus.exception;

public class StopNotFoundException extends RuntimeException {
    public StopNotFoundException(Long stopId) {
        super("Stop with id " + stopId + " not found");
    }
}