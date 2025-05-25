// StopSubscription.java
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
@Table(name = "StopSubscription")
@IdClass(StopSubscriptionId.class)
public class StopSubscription {
    @Id
    @Column(name = "user_id")
    private Long userId;

    @Id
    @Column(name = "stop_id")
    private Long stopId;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "stop_id", insertable = false, updatable = false)
    private Stop stop;
}