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
  /**
     * Finds all CommentUpvote entities by Comment.
     * 
     * @param Comment The Comment to search for
     * @return List of CommentUpvote entities matching the criteria
     */
    List<CommentUpvote> findByComment(Comment comment);

  /**
     * Finds all CommentUpvote entities by User.
     * 
     * @param User The User to search for
     * @return List of CommentUpvote entities matching the criteria
     */
    List<CommentUpvote> findByUser(User user);

  // Check if a user has upvoted a specific comment
  /**
     * Checks if an entity exists by UserIdAndCommentId.
     * 
     * @param UserIdAndCommentId The UserIdAndCommentId to check
     * @return true if entity exists, false otherwise
     */
    boolean existsByUserIdAndCommentId(Long userId, Long commentId);

  // Count upvotes for a specific comment
  /**
     * Counts entities by CommentId.
     * 
     * @param CommentId The CommentId to count by
     * @return Number of entities matching the criteria
     */
    Long countByCommentId(Long commentId);

  // Count upvotes by a specific user
  /**
     * Counts entities by UserId.
     * 
     * @param UserId The UserId to count by
     * @return Number of entities matching the criteria
     */
    Long countByUserId(Long userId);

  // Delete upvote by user and comment
  /**
     * Deletes entities by UserIdAndCommentId.
     * 
     * @param UserIdAndCommentId The UserIdAndCommentId to delete by
     */
    void deleteByUserIdAndCommentId(Long userId, Long commentId);

  // Find upvote by user and comment
  /**
     * Finds a CommentUpvote by UserIdAndCommentId.
     * 
     * @param UserIdAndCommentId The UserIdAndCommentId to search for
     * @return Optional containing the CommentUpvote if found
     */
    Optional<CommentUpvote> findByUserIdAndCommentId(Long userId, Long commentId);

  // Get all comments upvoted by a user (with pagination)
  /**
     * Custom query method with specific business logic.
     * 
     * @return Query result based on custom implementation
     */
    @Query("SELECT u.comment FROM CommentUpvote u WHERE u.userId = :userId")
    Page<Comment> findCommentsUpvotedByUser(@Param("userId") Long userId, Pageable pageable);
  // Get all users who upvoted a specific comment
  /**
     * Custom query method with specific business logic.
     * 
     * @return Query result based on custom implementation
     */
    @Query("SELECT u.user FROM CommentUpvote u WHERE u.commentId = :commentId")
    List<User> findUsersWhoUpvotedComment(@Param("commentId") Long commentId);
  // Get upvote statistics for multiple comments
  /**
     * Custom query method with specific business logic.
     * 
     * @return Query result based on custom implementation
     */
    @Query("SELECT u.commentId, COUNT(u) FROM CommentUpvote u WHERE u.commentId IN :commentIds GROUP BY u.commentId")
    List<Object[]> getUpvoteCountsForComments(@Param("commentIds") List<Long> commentIds);
}