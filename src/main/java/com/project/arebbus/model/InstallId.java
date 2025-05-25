// InstallId.java
package com.project.arebbus.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstallId implements Serializable {
    private Long userId;
    private Long busId;
}