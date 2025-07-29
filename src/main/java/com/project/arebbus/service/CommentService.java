package com.project.arebbus.service;

import com.project.arebbus.dto.CommentResponse;
import com.project.arebbus.model.Comment;
import com.project.arebbus.model.User;
import com.project.arebbus.repositories.CommentRepository;
import com.project.arebbus.repositories.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class for comment business logic operations.
 * Handles comment creation and management for posts.
 */
@Service
@RequiredArgsConstructor
public class CommentService {
    /** Repository for post data access */
    private final PostRepository postRepository;
    /** Repository for comment data access */
    private final CommentRepository commentRepository;

    /**
     * Creates a new comment on a post.
     * 
     * @param user The user creating the comment
     * @param content The comment content
     * @param postId The ID of the post to comment on
     * @return CommentResponse containing the created comment details
     * @throws IllegalArgumentException if the post doesn't exist
     */
    public CommentResponse createComment(User user, String content, Long postId) {
        var post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found with id: " + postId));

        var comment = commentRepository.save(
                Comment.builder()
                        .content(content)
                        .author(user)
                        .post(post)
                        .numUpvote(0L)
                        .build()
        );

        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .authorName(comment.getAuthor().getName())
                .postId(postId)
                .createdAt(comment.getCreatedAt())
                .build();
    }

}
