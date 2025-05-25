package com.project.arebbus.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteSubscriptionId implements Serializable {
    private Long userId;
    private Long routeId;
}