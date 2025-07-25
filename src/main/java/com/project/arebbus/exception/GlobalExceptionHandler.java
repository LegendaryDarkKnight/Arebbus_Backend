package com.project.arebbus.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<?> handlePostNotFoundException(PostNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse(e.getMessage()));
    }

    @ExceptionHandler(UnauthorizedPostAccessException.class)
    public ResponseEntity<?> handleUnauthorizedPostAccessException(UnauthorizedPostAccessException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse(e.getMessage()));
    }

    @ExceptionHandler(UserAlreadyExists.class)
    public ResponseEntity<?> handleUserAlreadyExists(UserAlreadyExists e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse(e.getMessage()));
    }

    @ExceptionHandler(UserNotFound.class)
    public ResponseEntity<?> handleUserNotFound(UserNotFound e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse(e.getMessage()));
    }

    @ExceptionHandler(StopNotFoundException.class)
    public ResponseEntity<?> handleStopNotFoundException(StopNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse(e.getMessage()));
    }

    @ExceptionHandler(RouteNotFoundException.class)
    public ResponseEntity<?> handleRouteNotFoundException(RouteNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse(e.getMessage()));
    }

    @ExceptionHandler(BusNotFoundException.class)
    public ResponseEntity<?> handleBusNotFoundException(BusNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse(e.getMessage()));
    }

    @ExceptionHandler(BusAlreadyInstalledException.class)
    public ResponseEntity<?> handleBusAlreadyInstalledException(BusAlreadyInstalledException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse(e.getMessage()));
    }

    @ExceptionHandler(BusNotInstalledException.class)
    public ResponseEntity<?> handleBusNotInstalledException(BusNotInstalledException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse(e.getMessage()));
    }

    @ExceptionHandler(LocationNotFoundException.class)
    public ResponseEntity<?> handleLocationNotFoundException(LocationNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse(e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        e.printStackTrace(System.err);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse(e.getMessage()));
    }

    private Map<String, Object> errorResponse(String message) {
        return Map.of(
                "timestamp", Instant.now().toString(),
                "error", message == null ? "An error occurred" : message
        );
    }
}
