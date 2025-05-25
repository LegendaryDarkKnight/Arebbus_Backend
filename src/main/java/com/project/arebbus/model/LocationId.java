// LocationId.java
package com.project.arebbus.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationId implements Serializable {
    private Long busId;
    private Long userId;
}