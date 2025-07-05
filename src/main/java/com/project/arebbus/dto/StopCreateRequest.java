package com.project.arebbus.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StopCreateRequest {
    private String name;
    private BigDecimal latitude;
    private BigDecimal longitude;
}