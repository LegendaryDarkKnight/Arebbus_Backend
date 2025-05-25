// Install.java
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
@Table(name = "Install")
@IdClass(InstallId.class)
public class Install {
    @Id
    @Column(name = "user_id")
    private Long userId;

    @Id
    @Column(name = "bus_id")
    private Long busId;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "bus_id", insertable = false, updatable = false)
    private Bus bus;
}