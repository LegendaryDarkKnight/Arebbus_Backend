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
}