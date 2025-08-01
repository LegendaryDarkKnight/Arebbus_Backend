package com.project.arebbus.service;

import com.project.arebbus.dto.ToggleUpvoteResponse;
import com.project.arebbus.model.CommentUpvote;
import com.project.arebbus.model.User;
import com.project.arebbus.repositories.CommentRepository;
import com.project.arebbus.repositories.CommentUpvoteRepository;
import jakarta.transaction.Transactional;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class ToggleCommentUpvoteService {
  /** Repository for data access */
  private final CommentRepository commentRepository;

  /** Repository for data access */
  private final CommentUpvoteRepository commentUpvoteRepository;

  /**
   * Toggles user interaction (upvote/downvote).
   *
   * @param user The user performing the action
   * @param request The toggle request
   * @return ToggleUpvoteResponse containing toggle status
   */
  public ToggleUpvoteResponse toggleUpvote(User user, Long commentId) {
    var comment =
        commentRepository
            .findById(commentId)
            .orElseThrow(
                () -> new IllegalArgumentException("Comment not found with id: " + commentId));

    if (commentUpvoteRepository.existsByUserIdAndCommentId(user.getId(), commentId)) {
      commentUpvoteRepository.deleteByUserIdAndCommentId(user.getId(), commentId);
      comment.setNumUpvote(commentUpvoteRepository.countByCommentId(commentId));
      return ToggleUpvoteResponse.builder().upvoteStatus(false).toggledAt(new Date()).build();
    }

    commentUpvoteRepository.save(
        CommentUpvote.builder()
            .userId(user.getId())
            .commentId(comment.getId())
            .user(user)
            .comment(comment)
            .build());
    comment.setNumUpvote(commentUpvoteRepository.countByCommentId(commentId));

    return ToggleUpvoteResponse.builder().upvoteStatus(true).toggledAt(new Date()).build();
  }
}
