package com.project.arebbus.model;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusUpvoteId implements Serializable {
    private Long busId;
    private Long userId;
}