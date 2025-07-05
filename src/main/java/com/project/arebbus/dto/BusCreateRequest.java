package com.project.arebbus.dto;

import lombok.Data;

@Data
public class BusCreateRequest {
    private String name;
    private Long routeId;
    private Short capacity;
}