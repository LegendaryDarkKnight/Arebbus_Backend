package com.project.arebbus.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;



@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Location")
@IdClass(LocationId.class)
public class Location {
    @Id
    @Column(name = "bus_id")
    private Long busId;

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal latitude;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal longitude;

    @Column(nullable = false)
    private LocalDateTime time;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LocationStatus status;

    @ManyToOne
    @JoinColumn(name = "bus_id", insertable = false, updatable = false)
    private Bus bus;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
}
