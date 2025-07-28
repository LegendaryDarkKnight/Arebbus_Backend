package com.project.arebbus.repositories;

import com.project.arebbus.model.Post;
import com.project.arebbus.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByAuthor(User author);
    List<Post> findByContentContainingIgnoreCase(String content);
    List<Post> findByOrderByNumUpvoteDesc();

    // Existing pagination methods
    Page<Post> findAll(Pageable pageable);
    Page<Post> findByAuthor(User author, Pageable pageable);

    // New paginated methods with filters
    @Query(nativeQuery = true, value =
            "SELECT * FROM Post p " +
                    "WHERE LOWER(p.content) LIKE LOWER(CONCAT('%', :content, '%')) " +
                    "ORDER BY p.num_upvote DESC")
    Page<Post> findByContentContainingIgnoreCase(@Param("content") String content, Pageable pageable);

    // Find posts by tag name with pagination
    @Query(nativeQuery = true, value =
            "SELECT DISTINCT p.* FROM Post p " +
                    "INNER JOIN post_tag pt ON p.id = pt.post_id " +
                    "INNER JOIN Tag t ON pt.tag_id = t.id " +
                    "WHERE t.name = :tagName")
    Page<Post> findPostsByTagName(@Param("tagName") String tagName, Pageable pageable);

    // Find posts by multiple tags with pagination (ALL tags must match)
    @Query(nativeQuery = true, value =
            "SELECT DISTINCT p.* FROM Post p " +
                    "INNER JOIN post_tag pt ON p.id = pt.post_id " +
                    "INNER JOIN Tag t ON pt.tag_id = t.id " +
                    "WHERE t.name IN :tagNames " +
                    "GROUP BY p.id " +
                    "HAVING COUNT(DISTINCT t.name) = :tagCount")
    Page<Post> findPostsByAllTagNames(@Param("tagNames") List<String> tagNames, @Param("tagCount") long tagCount, Pageable pageable);

    // Find posts by any of the specified tags with pagination
    @Query(nativeQuery = true, value =
            "SELECT DISTINCT p.* FROM Post p " +
                    "INNER JOIN post_tag pt ON p.id = pt.post_id " +
                    "INNER JOIN Tag t ON pt.tag_id = t.id " +
                    "WHERE t.name IN :tagNames")
    Page<Post> findPostsByAnyTagNames(@Param("tagNames") List<String> tagNames, Pageable pageable);

    // Find posts created after a certain date with pagination
    Page<Post> findByCreatedAtAfter(Date date, Pageable pageable);

    // Find posts created between dates with pagination
    Page<Post> findByCreatedAtBetween(Date startDate, Date endDate, Pageable pageable);

    // Complex query with multiple filters
    @Query(nativeQuery = true, value =
                    "SELECT DISTINCT p.* FROM Post p " +
                    "LEFT JOIN post_tag pt ON p.id = pt.post_id " +
                    "LEFT JOIN Tag t ON pt.tag_id = t.id " +
                    "WHERE (:content IS NULL OR LOWER(p.content) LIKE LOWER(CONCAT('%', :content, '%'))) " +
                    "AND (:authorId IS NULL OR p.author_id = :authorId) " +
                    "AND (:tagNames IS NULL OR t.name IN :tagNames) " +
                    "AND (:minUpvotes IS NULL OR p.num_upvote >= :minUpvotes) " +
                    "AND (:fromDate IS NULL OR p.created_at >= :fromDate) " +
                    "AND (:toDate IS NULL OR p.created_at <= :toDate)")
    Page<Post> findPostsWithFilters(
            @Param("content") String content,
            @Param("authorId") Long authorId,
            @Param("tagNames") List<String> tagNames,
            @Param("minUpvotes") Long minUpvotes,
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate,
            Pageable pageable
    );
}