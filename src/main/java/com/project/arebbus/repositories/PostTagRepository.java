package com.project.arebbus.repositories;

import com.project.arebbus.model.PostTag;
import com.project.arebbus.model.PostTagId;
import com.project.arebbus.model.Post;
import com.project.arebbus.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PostTagRepository extends JpaRepository<PostTag, PostTagId> {
    /**
     * Finds all PostTag entities by Post.
     * 
     * @param Post The Post to search for
     * @return List of PostTag entities matching the criteria
     */
    List<PostTag> findByPost(Post post);
    /**
     * Finds all PostTag entities by Tag.
     * 
     * @param Tag The Tag to search for
     * @return List of PostTag entities matching the criteria
     */
    List<PostTag> findByTag(Tag tag);
    /**
     * Checks if an entity exists by PostAndTag.
     * 
     * @param PostAndTag The PostAndTag to check
     * @return true if entity exists, false otherwise
     */
    boolean existsByPostAndTag(Post post, Tag tag);
}
