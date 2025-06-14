package com.project.arebbus.model;

import jakarta.persistence.*;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "comment_upvotes")
@IdClass(CommentUpvoteId.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentUpvote {
  @Id
  @Column(name = "user_id")
  private Long userId;

  @Id
  @Column(name = "comment_id")
  private Long commentId;

  @ManyToOne
  @JoinColumn(name = "user_id", insertable = false, updatable = false)
  private User user;

  @ManyToOne
  @JoinColumn(name = "comment_id", insertable = false, updatable = false)
  private Comment comment;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false, nullable = false)
  private Date createdAt;
}