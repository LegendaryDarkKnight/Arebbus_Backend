package com.project.arebbus.service;

import com.project.arebbus.dto.ToggleUpvoteResponse;
import com.project.arebbus.model.Upvote;
import com.project.arebbus.model.User;
import com.project.arebbus.repositories.PostRepository;
import com.project.arebbus.repositories.UpvoteRepository;
import jakarta.transaction.Transactional;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class ToggleUpvoteService {
  private final PostRepository postRepository;
  private final UpvoteRepository upvoteRepository;

  public ToggleUpvoteResponse toggleUpvote(User user, Long postId) {
    var post =
        postRepository
            .findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("Post not found with id: " + postId));

    if (upvoteRepository.existsByUserIdAndPostId(user.getId(), postId)) {
      upvoteRepository.deleteByUserIdAndPostId(user.getId(), postId);
      return ToggleUpvoteResponse.builder().upvoteStatus(false).toggledAt(new Date()).build();
    }

    var upvote =
        upvoteRepository.save(
            Upvote.builder()
                .userId(user.getId())
                .postId(post.getId())
                .user(user)
                .post(post)
                .build());

    System.out.println(upvote.getCreatedAt());

    return ToggleUpvoteResponse.builder()
        .upvoteStatus(true)
        .toggledAt(upvote.getCreatedAt())
        .build();
  }
}
