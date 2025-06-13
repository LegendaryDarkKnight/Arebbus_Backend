package com.project.arebbus.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class PostResponse {
    private Long postId;
    private String authorName;
    private String content;
    private Long numUpvote;
    private List<String> tags;
    private Date createdAt;
    private List<CommentResponse> comments;
    private boolean upvoted; // Indicates if the current user has upvoted this post
}
