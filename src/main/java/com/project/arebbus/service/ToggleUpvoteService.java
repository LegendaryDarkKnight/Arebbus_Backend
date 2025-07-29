package com.project.arebbus.service;

import com.project.arebbus.dto.ToggleUpvoteResponse;
import com.project.arebbus.model.Upvote;
import com.project.arebbus.model.User;
import com.project.arebbus.repositories.PostRepository;
import com.project.arebbus.repositories.UpvoteRepository;
import jakarta.transaction.Transactional;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class ToggleUpvoteService {
    /** Repository for  data access */
    private final PostRepository postRepository;
    /** Repository for  data access */
    private final UpvoteRepository upvoteRepository;

    /**
     * Toggles user interaction (upvote/downvote).
     * 
     * @param user The user performing the action
     * @param request The toggle request
     * @return ToggleUpvoteResponse containing toggle status
     */
    public ToggleUpvoteResponse toggle(User user, Long postId) {
        var post =
                postRepository
                        .findById(postId)
                        .orElseThrow(() -> new IllegalArgumentException("Post not found with id: " + postId));

        if (upvoteRepository.existsByUserIdAndPostId(user.getId(), postId)) {
            upvoteRepository.deleteByUserIdAndPostId(user.getId(), postId);
            post.setNumUpvote(upvoteRepository.countByPostId(postId));
            return ToggleUpvoteResponse.builder().upvoteStatus(false).toggledAt(new Date()).build();
        }

        upvoteRepository.save(
                Upvote.builder()
                        .userId(user.getId())
                        .postId(post.getId())
                        .user(user)
                        .post(post)
                        .build());
        post.setNumUpvote(upvoteRepository.countByPostId(postId));

        return ToggleUpvoteResponse.builder()
                .upvoteStatus(true)
                .toggledAt(new Date()) // Set the current date and time
                .build();
    }
}
