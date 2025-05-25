package com.project.arebbus.repositories;

import com.project.arebbus.model.Comment;
import com.project.arebbus.model.Post;
import com.project.arebbus.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost(Post post);
    List<Comment> findByAuthor(User author);
    List<Comment> findByPostOrderByNumUpvoteDesc(Post post);
    List<Comment> findByContentContainingIgnoreCase(String content);
}