// PostRepository.java
package com.project.arebbus.repositories;

import com.project.arebbus.model.Post;
import com.project.arebbus.model.User;
import com.project.arebbus.model.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByAuthor(User author);
    List<Post> findByContentContainingIgnoreCase(String content);
    List<Post> findByOrderByNumUpvoteDesc();

    // Find posts by tag (many-to-many through PostTag)
    @Query(nativeQuery = true,value = "SELECT p FROM Post p JOIN p.postTags pt WHERE pt.tag = :tag")
    List<Post> findPostsByTag(@Param("tag") Tag tag);

    // Find posts by tag name
    @Query(nativeQuery = true, value = "SELECT p FROM Post p JOIN p.postTags pt JOIN pt.tag t WHERE t.name = :tagName")
    List<Post> findPostsByTagName(@Param("tagName") String tagName);

    // Find posts with multiple tags
    @Query(nativeQuery = true, value = "SELECT p FROM Post p WHERE p.id IN (SELECT pt.postId FROM PostTag pt WHERE pt.tagId IN :tagIds GROUP BY pt.postId HAVING COUNT(pt.tagId) = :tagCount)")
    List<Post> findPostsWithAllTags(@Param("tagIds") List<Long> tagIds, @Param("tagCount") Long tagCount);

    // Find most popular posts
    @Query(nativeQuery = true, value = "SELECT p FROM Post p ORDER BY p.numUpvote DESC")
    List<Post> findMostPopularPosts();

    Page<Post> findAll(Pageable pageable);
}
