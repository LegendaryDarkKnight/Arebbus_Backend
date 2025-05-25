// WaitingFor.java
package com.project.arebbus.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "WaitingFor")
@IdClass(WaitingForId.class)
public class WaitingFor {
    @Id
    @Column(name = "user_id")
    private Long userId;

    @Id
    @Column(name = "bus_id")
    private Long busId;

    @Column(nullable = false)
    private Boolean valid;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "bus_id", insertable = false, updatable = false)
    private Bus bus;
}
