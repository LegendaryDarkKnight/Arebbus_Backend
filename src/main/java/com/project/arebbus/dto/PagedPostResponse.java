package com.project.arebbus.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class PagedPostResponse {
    private List<PostSummaryResponse> posts;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}
