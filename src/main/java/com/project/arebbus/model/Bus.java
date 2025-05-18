package com.project.arebbus.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "BUS")
public class Bus {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false) //foreign key
    private User author;

    private Integer capacity;
    private Long routeId;
    private Double fairs;
    private Long basedOn;
    private String status;
    private Integer installs;
    private Integer upvotes;

    @ManyToMany(mappedBy = "installedBuses")
    private Set<User> installedByUsers;

}
