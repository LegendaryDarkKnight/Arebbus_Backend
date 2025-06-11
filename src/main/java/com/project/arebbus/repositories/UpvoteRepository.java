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
  List<Upvote> findByPost(Post post);

  List<Upvote> findByUser(User user);

  // Check if a user has upvoted a specific post
  boolean existsByUserIdAndPostId(Long userId, Long postId);

  // Count upvotes for a specific post
  long countByPostId(Long postId);

  // Count upvotes by a specific user
  long countByUserId(Long userId);

  // Delete upvote by user and post
  void deleteByUserIdAndPostId(Long userId, Long postId);

  // Find upvote by user and post
  Optional<Upvote> findByUserIdAndPostId(Long userId, Long postId);

  // Get all posts upvoted by a user (with pagination)
  @Query("SELECT u.post FROM Upvote u WHERE u.userId = :userId")
  Page<Post> findPostsUpvotedByUser(@Param("userId") Long userId, Pageable pageable);

  // Get all users who upvoted a specific post
  @Query("SELECT u.user FROM Upvote u WHERE u.postId = :postId")
  List<User> findUsersWhoUpvotedPost(@Param("postId") Long postId);

  // Get upvote statistics for multiple posts
  @Query("SELECT u.postId, COUNT(u) FROM Upvote u WHERE u.postId IN :postIds GROUP BY u.postId")
  List<Object[]> getUpvoteCountsForPosts(@Param("postIds") List<Long> postIds);
}
