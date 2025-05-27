package com.project.arebbus.controller;

import com.project.arebbus.dto.CommentRequest;
import com.project.arebbus.dto.CommentResponse;
import com.project.arebbus.model.User;
import com.project.arebbus.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {
    private final CommentService commentService;


    @PostMapping
    public ResponseEntity<CommentResponse> createComment(@RequestBody CommentRequest commentRequest, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        CommentResponse commentResponse = commentService.createComment(user, commentRequest.getContent(), commentRequest.getPostId());
        return ResponseEntity.ok(commentResponse);
    }

}
