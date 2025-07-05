package com.project.arebbus.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class RouteResponse {
    private Long id;
    private String name;
    private String authorName;
    private List<StopResponse> stops;
}