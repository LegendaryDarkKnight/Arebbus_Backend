package com.project.arebbus.exception;

public class BusAlreadyInstalledException extends RuntimeException {
    public BusAlreadyInstalledException(Long busId) {
        super("Bus with id " + busId + " is already installed");
    }
}