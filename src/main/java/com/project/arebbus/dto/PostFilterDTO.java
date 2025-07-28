package com.project.arebbus.dto;

import lombok.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostFilterDTO {
    private String content;
    private List<String> tags;
    private Long authorId;
    private String sortBy; // "recent", "popular", "oldest"
    private Long minUpvotes;
    private Date fromDate;
    private Date toDate;
    private Boolean tagsMatchAll; // true = AND condition, false = OR condition

    // Pagination parameters
    @Builder.Default
    private int page = 0;
    @Builder.Default
    private int size = 10;

    public Pageable toPageable() {
        Sort sort = getSort();
        return PageRequest.of(page, size, sort);
    }

    private Sort getSort() {
        if (sortBy == null) {
            return Sort.by(Sort.Direction.DESC, "created_at"); // Default: most recent
        }

        return switch (sortBy.toLowerCase()) {
            case "popular" -> Sort.by(Sort.Direction.DESC, "num_upvote");
            case "oldest" -> Sort.by(Sort.Direction.ASC, "created_at");
            default -> Sort.by(Sort.Direction.DESC, "created_at");
        };
    }
}