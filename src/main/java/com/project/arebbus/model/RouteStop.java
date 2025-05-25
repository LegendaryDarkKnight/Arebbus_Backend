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
@Table(name = "RouteStop")
@IdClass(RouteStopId.class)
public class RouteStop {
    @Id
    @Column(name = "route_id")
    private Long routeId;

    @Id
    @Column(name = "stop_id")
    private Long stopId;

    @Column(name = "stop_index", nullable = false)
    private Long stopIndex;

//    @ManyToOne
//    @JoinColumn(name = "author_id", nullable = false)
//    private User author;

    @ManyToOne
    @JoinColumn(name = "route_id", insertable = false, updatable = false)
    private Route route;

    @ManyToOne
    @JoinColumn(name = "stop_id", insertable = false, updatable = false)
    private Stop stop;
}
