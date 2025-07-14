package com.project.arebbus.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class BusResponse {
    private Long id;
    private String name;
    private String authorName;
    private RouteResponse route;
    private Short capacity;
    private Integer numInstall;
    private Long numUpvote;
    private String status;
    private BusResponse basedOn;
    private boolean upvoted;
    private boolean installed;
}