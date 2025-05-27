package com.project.arebbus.controller;

import com.project.arebbus.dto.*;
import com.project.arebbus.model.User;
import com.project.arebbus.service.UserPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

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
    public ResponseEntity<UserPostCreateResponse> createPost(@RequestBody UserPostCreateRequest userPostCreateRequest, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(userPostService.createPost(user, userPostCreateRequest.getContent(), userPostCreateRequest.getTags()));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<UserPostDeleteResponse> deletePost(@RequestBody UserPostDeleteRequest userPostDeleteRequest, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(userPostService.deletePost(user, userPostDeleteRequest.getPostId()));
    }

    @GetMapping("/all")
    public ResponseEntity<PagedPostResponse> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        return ResponseEntity.ok(userPostService.getAllPostsPage(page, size));
    }


    @GetMapping
    public ResponseEntity<PostSummaryResponse> getPostById(@RequestParam Long postId, Authentication authentication) {
        return ResponseEntity.ok(userPostService.getPostById(postId));
    }

}
