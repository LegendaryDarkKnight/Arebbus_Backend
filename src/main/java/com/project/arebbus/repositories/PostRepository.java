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
    /**
     * Finds all Post entities by Author.
     * 
     * @param Author The Author to search for
     * @return List of Post entities matching the criteria
     */
    List<Post> findByAuthor(User author);
    /**
     * Finds all Post entities by ContentContainingIgnoreCase.
     * 
     * @param ContentContainingIgnoreCase The ContentContainingIgnoreCase to search for
     * @return List of Post entities matching the criteria
     */
    List<Post> findByContentContainingIgnoreCase(String content);
    /**
     * Finds all Post entities by OrderByNumUpvoteDesc.
     * 
     * @param OrderByNumUpvoteDesc The OrderByNumUpvoteDesc to search for
     * @return List of Post entities matching the criteria
     */
    List<Post> findByOrderByNumUpvoteDesc();

    // Find posts by tag (many-to-many through PostTag)
    /**
     * Custom query method with specific business logic.
     * 
     * @return Query result based on custom implementation
     */
    @Query(nativeQuery = true,value = "SELECT p FROM Post p JOIN p.postTags pt WHERE pt.tag = :tag")
    List<Post> findPostsByTag(@Param("tag") Tag tag);

    // Find posts by tag name
    /**
     * Custom query method with specific business logic.
     * 
     * @return Query result based on custom implementation
     */
    @Query(nativeQuery = true, value = "SELECT p FROM Post p JOIN p.postTags pt JOIN pt.tag t WHERE t.name = :tagName")
    List<Post> findPostsByTagName(@Param("tagName") String tagName);

    // Find posts with multiple tags
    /**
     * Custom query method with specific business logic.
     * 
     * @return Query result based on custom implementation
     */
    @Query(nativeQuery = true, value = "SELECT p FROM Post p WHERE p.id IN (SELECT pt.postId FROM PostTag pt WHERE pt.tagId IN :tagIds GROUP BY pt.postId HAVING COUNT(pt.tagId) = :tagCount)")
    List<Post> findPostsWithAllTags(@Param("tagIds") List<Long> tagIds, @Param("tagCount") Long tagCount);

    // Find most popular posts
    /**
     * Custom query method with specific business logic.
     * 
     * @return Query result based on custom implementation
     */
    @Query(nativeQuery = true, value = "SELECT p FROM Post p ORDER BY p.numUpvote DESC")
    List<Post> findMostPopularPosts();

    Page<Post> findAll(Pageable pageable);
    /**
     * Finds all Post entities by Author with pagination.
     * 
     * @param Author The Author to search for
     * @param pageable The pagination information
     * @return Page of Post entities matching the criteria
     */
    Page<Post> findByAuthor(User author, Pageable pageable);

    // Find posts by tag names with pagination and sorting
    /**
     * Custom query method with specific business logic.
     * 
     * @return Query result based on custom implementation
     */
     @Query("SELECT DISTINCT p FROM Post p JOIN p.postTags pt JOIN pt.tag t WHERE t.name IN :tagNames")
    Page<Post> findByTagNames(@Param("tagNames") List<String> tagNames, Pageable pageable);
}
