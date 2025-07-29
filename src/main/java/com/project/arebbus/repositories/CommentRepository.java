package com.project.arebbus.repositories;

import com.project.arebbus.model.Comment;
import com.project.arebbus.model.Post;
import com.project.arebbus.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    /**
     * Finds all Comment entities by Post.
     * 
     * @param Post The Post to search for
     * @return List of Comment entities matching the criteria
     */
    List<Comment> findByPost(Post post);
    /**
     * Finds all Comment entities by Author.
     * 
     * @param Author The Author to search for
     * @return List of Comment entities matching the criteria
     */
    List<Comment> findByAuthor(User author);
    /**
     * Finds all Comment entities by PostOrderByNumUpvoteDesc.
     * 
     * @param PostOrderByNumUpvoteDesc The PostOrderByNumUpvoteDesc to search for
     * @return List of Comment entities matching the criteria
     */
    List<Comment> findByPostOrderByNumUpvoteDesc(Post post);
    /**
     * Finds all Comment entities by ContentContainingIgnoreCase.
     * 
     * @param ContentContainingIgnoreCase The ContentContainingIgnoreCase to search for
     * @return List of Comment entities matching the criteria
     */
    List<Comment> findByContentContainingIgnoreCase(String content);
}