package com.project.arebbus.controller;

import com.project.arebbus.dto.*;
import com.project.arebbus.model.User;
import com.project.arebbus.service.UserPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.Date;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class UserPostController {

    private final UserPostService userPostService;

    @GetMapping("/test")
    public String getMethodName(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return "Hello, " + user.getName();
    }

    @PostMapping("/create")
    public ResponseEntity<UserPostCreateResponse> createPost(@RequestBody UserPostCreateRequest request, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(userPostService.createPost(user, request));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<UserPostDeleteResponse> deletePost(@RequestBody UserPostDeleteRequest request, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(userPostService.deletePost(user, request));
    }

    @GetMapping("/all")
    public ResponseEntity<PagedPostResponse> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(userPostService.getAllPostsPage(user, page, size));
    }

    @GetMapping("/my")
    public ResponseEntity<PagedPostResponse> getMyPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(userPostService.getMyPostsPage(user, page, size));
    }


    @GetMapping
    public ResponseEntity<PostResponse> getPostById(@RequestParam Long postId, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(userPostService.getPostById(user, postId));
    }

    @GetMapping("/search")
    public ResponseEntity<PagedPostResponse> searchPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) List<String> tags,
            @RequestParam(required = false) Long authorId,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) Long minUpvotes,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate,
            @RequestParam(defaultValue = "false") Boolean tagsMatchAll,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        PostFilterDTO filter = PostFilterDTO.builder()
                .page(page)
                .size(size)
                .content(content)
                .tags(tags)
                .authorId(authorId)
                .sortBy(sortBy)
                .minUpvotes(minUpvotes)
                .fromDate(fromDate)
                .toDate(toDate)
                .tagsMatchAll(tagsMatchAll)
                .build();

        return ResponseEntity.ok(userPostService.getFilteredPostsPage(user, filter));
    }

    // Specific endpoints for common use cases
    @GetMapping("/by-tag")
    public ResponseEntity<PagedPostResponse> getPostsByTag(
            @RequestParam String tag,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "recent") String sortBy,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(userPostService.getPostsByTag(user, tag, page, size, sortBy));
    }

    @GetMapping("/by-content")
    public ResponseEntity<PagedPostResponse> getPostsByContent(
            @RequestParam String content,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "recent") String sortBy,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(userPostService.getPostsByContent(user, content, page, size, sortBy));
    }

    @GetMapping("/recent")
    public ResponseEntity<PagedPostResponse> getRecentPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(userPostService.getRecentPosts(user, page, size));
    }

    @GetMapping("/popular")
    public ResponseEntity<PagedPostResponse> getPopularPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(userPostService.getPopularPosts(user, page, size));
    }

}
