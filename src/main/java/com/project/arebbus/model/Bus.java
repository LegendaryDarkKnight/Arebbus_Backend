package com.project.arebbus.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Bus")
public class Bus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @ManyToOne
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @Column(nullable = false)
    private Short capacity;

    @Column(name = "num_install", nullable = false)
    private Integer numInstall;

    @Column(name = "num_upvote", nullable = false)
    private Long numUpvote;

    @Nullable
    private String status;

    @Column(name = "rating")
    private Double rating;

    @ManyToOne
    @JoinColumn(name = "based_on")
    private Bus basedOn;

    @OneToMany(mappedBy = "basedOn")
    private Set<Bus> derivedBuses;

    @OneToMany(mappedBy = "bus")
    private Set<Install> installs;

    @OneToMany(mappedBy = "bus")
    private Set<Location> locations;

    @OneToMany(mappedBy = "bus")
    private Set<WaitingFor> waitingUsers;
}