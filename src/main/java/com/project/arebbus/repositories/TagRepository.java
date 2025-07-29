package com.project.arebbus.repositories;

import com.project.arebbus.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    /**
     * Finds a Tag by Name.
     * 
     * @param Name The Name to search for
     * @return Optional containing the Tag if found
     */
    Optional<Tag> findByName(String name);
    /**
     * Checks if an entity exists by Name.
     * 
     * @param Name The Name to check
     * @return true if entity exists, false otherwise
     */
    boolean existsByName(String name);
    /**
     * Finds all Tag entities by NameContainingIgnoreCase.
     * 
     * @param NameContainingIgnoreCase The NameContainingIgnoreCase to search for
     * @return List of Tag entities matching the criteria
     */
    List<Tag> findByNameContainingIgnoreCase(String name);

    // Find tags used in a specific post
    /**
     * Custom query method with specific business logic.
     * 
     * @return Query result based on custom implementation
     */
    @Query
    List<Tag> findTagsByPostId(@Param("postId") Long postId);

    // Find most popular tags
    /**
     * Custom query method with specific business logic.
     * 
     * @return Query result based on custom implementation
     */
    @Query
    DESC")
    List<Tag> findMostPopularTags();
}