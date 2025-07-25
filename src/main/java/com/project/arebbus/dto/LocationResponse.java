package com.project.arebbus.dto;

import com.project.arebbus.model.LocationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationResponse {
    private Long userId;
    private Long busId;
    private String busName;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private LocalDateTime time;
    private LocationStatus status;
}