package com.project.arebbus.repositories;

import com.project.arebbus.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByName(String name);
    boolean existsByName(String name);
    List<Tag> findByNameContainingIgnoreCase(String name);

    // Find tags used in a specific post
    @Query(nativeQuery = true,
            value=
            """
            SELECT t FROM Tag
            t JOIN t.postTags pt
            WHERE pt.post.id = :postId
            """
    )
    List<Tag> findTagsByPostId(@Param("postId") Long postId);

    // Find most popular tags
    @Query(nativeQuery = true,
            value =
            """
            SELECT t FROM Tag
            t LEFT JOIN t.postTags pt
            GROUP BY t ORDER BY COUNT(pt) DESC
            """
    )
    List<Tag> findMostPopularTags();
}