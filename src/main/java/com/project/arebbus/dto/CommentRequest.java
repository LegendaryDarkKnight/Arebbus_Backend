package com.project.arebbus.dto;

import lombok.Data;

import java.util.List;

@Data
public class CommentRequest {
    private String content;
    private Long postId;
}

