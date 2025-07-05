package com.project.arebbus.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class PagedStopResponse {
    private List<StopResponse> stops;
    private int page;
    private int size;
    private int totalPages;
    private long totalElements;
}