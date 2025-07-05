package com.project.arebbus.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class BusInstallResponse {
    private Long busId;
    private String busName;
    private String message;
    private boolean installed;
}