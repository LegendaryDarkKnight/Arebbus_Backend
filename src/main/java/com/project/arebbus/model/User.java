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
@Table(name = "_user")
public class User  {
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
}
