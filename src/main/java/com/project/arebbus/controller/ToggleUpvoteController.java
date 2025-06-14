package com.project.arebbus.controller;

import com.project.arebbus.dto.ToggleUpvoteRequest;
import com.project.arebbus.dto.ToggleUpvoteResponse;
import com.project.arebbus.model.User;
import com.project.arebbus.service.ToggleCommentUpvoteService;
import com.project.arebbus.service.ToggleUpvoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/upvote")
public class ToggleUpvoteController {
  private final ToggleUpvoteService upvoteService;
  private final ToggleCommentUpvoteService commentUpvoteService;

  @PostMapping("/post")
  public ResponseEntity<ToggleUpvoteResponse> togglePostUpvote(
      @RequestBody ToggleUpvoteRequest request, Authentication authentication) {
    User user = (User) authentication.getPrincipal();
    ToggleUpvoteResponse response = upvoteService.toggleUpvote(user, request.getId());
    return ResponseEntity.ok(response);
  }

  @PostMapping("/comment")
  public ResponseEntity<ToggleUpvoteResponse> toggleCommentUpvote(
          @RequestBody ToggleUpvoteRequest request, Authentication authentication) {
    User user = (User) authentication.getPrincipal();
    ToggleUpvoteResponse response = commentUpvoteService.toggleUpvote(user, request.getId());
    return ResponseEntity.ok(response);
  }
}
