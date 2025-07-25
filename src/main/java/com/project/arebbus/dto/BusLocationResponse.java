package com.project.arebbus.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusLocationResponse {
    private Long busId;
    private String busName;
    private List<BusLocationCluster> locations;
}