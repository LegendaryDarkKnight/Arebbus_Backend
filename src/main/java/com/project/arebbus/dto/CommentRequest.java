package com.project.arebbus.dto;

import lombok.Data;

@Data
public class CommentRequest {
  private String content;
  private Long postId;
}
