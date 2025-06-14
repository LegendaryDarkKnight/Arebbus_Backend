package com.project.arebbus.repositories;

import com.project.arebbus.model.Comment;
import com.project.arebbus.model.CommentUpvote;
import com.project.arebbus.model.CommentUpvoteId;
import com.project.arebbus.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentUpvoteRepository extends JpaRepository<CommentUpvote, CommentUpvoteId> {
  List<CommentUpvote> findByComment(Comment comment);

  List<CommentUpvote> findByUser(User user);

  // Check if a user has upvoted a specific comment
  boolean existsByUserIdAndCommentId(Long userId, Long commentId);

  // Count upvotes for a specific comment
  long countByCommentId(Long commentId);

  // Count upvotes by a specific user
  long countByUserId(Long userId);

  // Delete upvote by user and comment
  void deleteByUserIdAndCommentId(Long userId, Long commentId);

  // Find upvote by user and comment
  Optional<CommentUpvote> findByUserIdAndCommentId(Long userId, Long commentId);

  // Get all comments upvoted by a user (with pagination)
  @Query("SELECT u.comment FROM CommentUpvote u WHERE u.userId = :userId")
  Page<Comment> findCommentsUpvotedByUser(@Param("userId") Long userId, Pageable pageable);

  // Get all users who upvoted a specific comment
  @Query("SELECT u.user FROM CommentUpvote u WHERE u.commentId = :commentId")
  List<User> findUsersWhoUpvotedComment(@Param("commentId") Long commentId);

  // Get upvote statistics for multiple comments
  @Query("SELECT u.commentId, COUNT(u) FROM CommentUpvote u WHERE u.commentId IN :commentIds GROUP BY u.commentId")
  List<Object[]> getUpvoteCountsForComments(@Param("commentIds") List<Long> commentIds);
}