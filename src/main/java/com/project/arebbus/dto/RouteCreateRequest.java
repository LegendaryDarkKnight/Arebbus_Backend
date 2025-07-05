package com.project.arebbus.dto;

import lombok.Data;

import java.util.List;

@Data
public class RouteCreateRequest {
    private String name;
    private List<Long> stopIds;
}