// RouteStopId.java
package com.project.arebbus.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteStopId implements Serializable {
    private Long routeId;
    private Long stopId;
}