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
@Table(name = "RouteSubscription")
@IdClass(RouteSubscriptionId.class)
public class RouteSubscription {
    @Id
    @Column(name = "user_id")
    private Long userId;

    @Id
    @Column(name = "route_id")
    private Long routeId;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "route_id", insertable = false, updatable = false)
    private Route route;
}
