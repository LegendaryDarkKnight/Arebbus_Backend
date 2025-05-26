package com.project.arebbus.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class UserPostCreateResponse {
    private Long postId;
    private String content;
    private String authorName;
    private List<String> tags;
}
