package com.project.arebbus.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "Post")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(nullable = false)
    private String content;

    @Column(name = "num_upvote", nullable = false)
    private Long numUpvote;

    @OneToMany(mappedBy = "post")
    private Set<PostTag> postTags;

    @OneToMany(mappedBy = "post")
    private Set<Comment> comments;

    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private Date createdAt;
}