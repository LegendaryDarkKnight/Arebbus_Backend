package com.project.arebbus.model;

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
@Table(name = "AREBBUS_USER")
public class User {
    @Id
    @GeneratedValue
    private Long id;

    private String fullName;
    private String userName;
    private String email;
    private String password;
    private Double reputation;
    private String image;
    private Boolean valid;

    @OneToMany(mappedBy = "author")
    private Set<Bus> authoredBuses;

    @ManyToMany
    @JoinTable(
            name = "user_installed_buses",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "bus_id")
    )
    private Set<Bus> installedBuses;


    @ManyToMany
    @JoinTable(
            name = "subscriptions",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "route_id")
    )
    private Set<Route> routeSubscriptions;
}
