package com.project.arebbus.exception;

public class UserAlreadyExists extends RuntimeException {
    public UserAlreadyExists(String email) {
        super("User with email " + email + " already exists");
    }
}
