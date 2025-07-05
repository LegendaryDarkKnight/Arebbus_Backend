package com.project.arebbus.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
public class StopResponse {
    private Long id;
    private String name;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String authorName;
}