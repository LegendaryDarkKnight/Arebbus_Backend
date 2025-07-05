package com.project.arebbus.exception;

public class BusNotInstalledException extends RuntimeException {
    public BusNotInstalledException(Long busId) {
        super("Bus with id " + busId + " is not installed");
    }
}