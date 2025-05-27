package com.project.arebbus.service;

import com.project.arebbus.dto.CommentResponse;
import com.project.arebbus.model.Comment;
import com.project.arebbus.model.User;
import com.project.arebbus.repositories.CommentRepository;
import com.project.arebbus.repositories.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;


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
