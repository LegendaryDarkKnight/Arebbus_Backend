package com.project.arebbus.exception;

public class UnauthorizedPostAccessException extends RuntimeException {
    public UnauthorizedPostAccessException(Long postId) {
        super("User not authorized to modify post " + postId);
    }
}
