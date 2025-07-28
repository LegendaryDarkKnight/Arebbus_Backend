package com.project.arebbus.controller;

import com.project.arebbus.dto.*;
import com.project.arebbus.model.User;
import com.project.arebbus.service.UserPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

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

    @GetMapping("/by-tags")
    public ResponseEntity<PagedPostResponse> getPostsByTags(
            @RequestParam List<String> tags,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(userPostService.getPostsByTags(user, tags, page, size));
    }

    @GetMapping("/tags")
    public ResponseEntity<List<String>> getAllTags() {
        return ResponseEntity.ok(userPostService.getAllTags());
    }

}
