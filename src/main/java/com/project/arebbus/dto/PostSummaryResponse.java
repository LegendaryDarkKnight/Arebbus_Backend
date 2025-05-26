package com.project.arebbus.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class PostSummaryResponse {
    private Long postId;
    private String authorName;
    private String content;
    private Long numUpvote;
    private List<String> tags;
}
