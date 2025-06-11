package com.project.arebbus.model;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpvoteId implements Serializable {
  private Long postId;
  private Long userId;
}
