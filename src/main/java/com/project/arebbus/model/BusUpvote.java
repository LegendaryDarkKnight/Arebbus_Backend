package com.project.arebbus.model;

import jakarta.persistence.*;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "bus_upvotes")
@IdClass(BusUpvoteId.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusUpvote {
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

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private Date createdAt;
}