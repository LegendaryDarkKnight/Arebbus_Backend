package com.project.arebbus.repositories;

import com.project.arebbus.model.Post;
import com.project.arebbus.model.Upvote;
import com.project.arebbus.model.UpvoteId;
import com.project.arebbus.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UpvoteRepository extends JpaRepository<Upvote, UpvoteId> {
  /**
     * Finds all Upvote entities by Post.
     * 
     * @param Post The Post to search for
     * @return List of Upvote entities matching the criteria
     */
    List<Upvote> findByPost(Post post);

  /**
     * Finds all Upvote entities by User.
     * 
     * @param User The User to search for
     * @return List of Upvote entities matching the criteria
     */
    List<Upvote> findByUser(User user);

  // Check if a user has upvoted a specific post
  /**
     * Checks if an entity exists by UserIdAndPostId.
     * 
     * @param UserIdAndPostId The UserIdAndPostId to check
     * @return true if entity exists, false otherwise
     */
    boolean existsByUserIdAndPostId(Long userId, Long postId);

  // Count upvotes for a specific post
  /**
     * Counts entities by PostId.
     * 
     * @param PostId The PostId to count by
     * @return Number of entities matching the criteria
     */
    Long countByPostId(Long postId);

  // Count upvotes by a specific user
  /**
     * Counts entities by UserId.
     * 
     * @param UserId The UserId to count by
     * @return Number of entities matching the criteria
     */
    Long countByUserId(Long userId);

  // Delete upvote by user and post
  /**
     * Deletes entities by UserIdAndPostId.
     * 
     * @param UserIdAndPostId The UserIdAndPostId to delete by
     */
    void deleteByUserIdAndPostId(Long userId, Long postId);

  // Find upvote by user and post
  /**
     * Finds a Upvote by UserIdAndPostId.
     * 
     * @param UserIdAndPostId The UserIdAndPostId to search for
     * @return Optional containing the Upvote if found
     */
    Optional<Upvote> findByUserIdAndPostId(Long userId, Long postId);

  // Get all posts upvoted by a user (with pagination)
  /**
     * Custom query method with specific business logic.
     * 
     * @return Query result based on custom implementation
     */
     @Query("SELECT u.post FROM Upvote u WHERE u.userId = :userId")
    Page<Post> findPostsUpvotedByUser(@Param("userId") Long userId, Pageable pageable);

  // Get all users who upvoted a specific post
  /**
     * Custom query method with specific business logic.
     * 
     * @return Query result based on custom implementation
     */
    @Query("SELECT u.user FROM Upvote u WHERE u.postId = :postId")
    List<User> findUsersWhoUpvotedPost(@Param("postId") Long postId);

  // Get upvote statistics for multiple posts
  /**
     * Custom query method with specific business logic.
     * 
     * @return Query result based on custom implementation
     */
    @Query("SELECT u.postId, COUNT(u) FROM Upvote u WHERE u.postId IN :postIds GROUP BY u.postId")
    List<Object[]> getUpvoteCountsForPosts(@Param("postIds") List<Long> postIds);
}
