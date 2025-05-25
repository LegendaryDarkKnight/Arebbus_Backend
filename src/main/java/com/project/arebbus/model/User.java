package com.project.arebbus.model;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.math.BigDecimal;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(unique = true, length = 255, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String image;

    @Column(nullable = false)
    private Integer reputation;

    @Column(nullable = false)
    private Boolean valid;

    @Column(precision = 8, scale = 2)
    private BigDecimal latitude;

    @Column(precision = 8, scale = 2)
    private BigDecimal longitude;

    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;

    // Relationships
    @OneToMany(mappedBy = "author")
    private Set<Bus> authoredBuses;

    @OneToMany(mappedBy = "author")
    private Set<Route> authoredRoutes;

    @OneToMany(mappedBy = "author")
    private Set<Stop> authoredStops;

//    @OneToMany(mappedBy = "author")
//    private Set<RouteStop> authoredRouteStops;

    @OneToMany(mappedBy = "author")
    private Set<Post> authoredPosts;

    @OneToMany(mappedBy = "author")
    private Set<Comment> authoredComments;

    @OneToMany(mappedBy = "user")
    private Set<Install> installations;

    @OneToMany(mappedBy = "user")
    private Set<RouteSubscription> routeSubscriptions;

    @OneToMany(mappedBy = "user")
    private Set<StopSubscription> stopSubscriptions;

    @OneToMany(mappedBy = "user")
    private Set<Location> locations;

    @OneToMany(mappedBy = "user")
    private Set<WaitingFor> waitingFor;

    // Spring Security UserDetails implementation
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return valid != null ? valid : true;
    }
}