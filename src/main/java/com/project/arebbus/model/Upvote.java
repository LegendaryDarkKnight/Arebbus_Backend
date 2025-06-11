package com.project.arebbus.model;

import jakarta.persistence.*;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "upvotes")
@IdClass(UpvoteId.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Upvote {
  @Id
  @Column(name = "user_id")
  private Long userId;

  @Id
  @Column(name = "post_id")
  private Long postId;

  @ManyToOne
  @JoinColumn(name = "user_id", insertable = false, updatable = false)
  private User user;

  @ManyToOne
  @JoinColumn(name = "post_id", insertable = false, updatable = false)
  private Post post;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false, nullable = false)
  private Date createdAt;
}
