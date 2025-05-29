package com.project.arebbus.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private Long userId;
    private String message;
    private boolean success;
    private String token;
    private String username;
    private String imageUrl;
}
