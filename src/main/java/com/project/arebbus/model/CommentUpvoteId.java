package com.project.arebbus.model;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentUpvoteId implements Serializable {
  private Long commentId;
  private Long userId;
}