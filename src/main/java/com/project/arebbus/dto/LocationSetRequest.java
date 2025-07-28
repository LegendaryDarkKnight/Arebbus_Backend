package com.project.arebbus.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationSetRequest {
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Long busId;
}