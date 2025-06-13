package com.project.arebbus.service;

import com.project.arebbus.dto.*;
import com.project.arebbus.exception.PostNotFoundException;
import com.project.arebbus.exception.UnauthorizedPostAccessException;
import com.project.arebbus.model.Post;
import com.project.arebbus.model.PostTag;
import com.project.arebbus.model.Tag;
import com.project.arebbus.model.User;
import com.project.arebbus.repositories.*;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.simple.SimpleLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserPostService {

    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final PostTagRepository postTagRepository;
    private final CommentRepository commentRepository;
    private final UpvoteRepository upvoteRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserPostService.class);


    public UserPostCreateResponse createPost(User user, UserPostCreateRequest request) {
        String content = request.getContent();
        List<String> tagNames = request.getTags();

        Post post = Post.builder()
                .author(user)
                .content(content)
                .numUpvote(0L)
                .build();

        Post savedPost = postRepository.save(post);

        List<String> savedTagNames = new ArrayList<>();

        for (String tagName : tagNames) {
            Tag tag = tagRepository.findByName(tagName)
                    .orElseGet(
                            () -> tagRepository.save(
                                    Tag.builder()
                                            .name(tagName)
                                            .build()
                            )
                    );
            PostTag postTag = PostTag.builder()
                    .postId(post.getId())
                    .tagId(tag.getId())
                    .post(post)
                    .tag(tag)
                    .build();
            postTagRepository.save(postTag);
            savedTagNames.add(tagName);
        }

        return UserPostCreateResponse.builder()
                .postId(savedPost.getId())
                .content(savedPost.getContent())
                .authorName(user.getName())
                .tags(savedTagNames)
                .createdAt(savedPost.getCreatedAt())
                .build();
    }


    public UserPostDeleteResponse deletePost(User user, UserPostDeleteRequest request) {
        Long postId = request.getPostId();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        if (!post.getAuthor().getId().equals(user.getId())) {
            throw new UnauthorizedPostAccessException(postId);
        }

        postTagRepository.deleteAll(postTagRepository.findByPost(post));
        postRepository.delete(post);

        return UserPostDeleteResponse.builder()
                .postId(postId)
                .message("Post deleted successfully")
                .build();
    }


    public PagedPostResponse getAllPostsPage(User user, int page, int size) {
        Page<Post> posts = postRepository.findAll(PageRequest.of(page, size));

        LOGGER.debug("Total posts found: {}", posts.getTotalElements());


        List<PostSummaryResponse> postSummaries = posts.getContent().stream().map(
                post -> PostSummaryResponse.builder()
                        .postId(post.getId())
                        .authorName(post.getAuthor().getName())
                        .content(post.getContent())
                        .numUpvote(post.getNumUpvote())
                        .createdAt(post.getCreatedAt())
                        .tags(post.getPostTags().stream()
                                .map(PostTag::getTag)
                                .map(Tag::getName)
                                .toList())
                        .upvoted(upvoteRepository.existsByUserIdAndPostId(user.getId(), post.getId()))
                        .build()
        ).toList();


        return PagedPostResponse.builder()
                .posts(postSummaries)
                .page(posts.getNumber())
                .size(posts.getSize())
                .totalPages(posts.getTotalPages())
                .totalElements(posts.getTotalElements())
                .build();
    }

    public PostResponse getPostById(User user, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        return PostResponse.builder()
                .postId(post.getId())
                .authorName(post.getAuthor().getName())
                .content(post.getContent())
                .numUpvote(post.getNumUpvote())
                .createdAt(post.getCreatedAt())
                .tags(post.getPostTags().stream()
                        .map(PostTag::getTag)
                        .map(Tag::getName)
                        .toList())
                .comments(commentRepository.findByPost(post).stream().map(
                        comment -> CommentResponse.builder()
                                .id(comment.getId())
                                .content(comment.getContent())
                                .authorName(comment.getAuthor().getName())
                                .postId(postId)
                                .createdAt(comment.getCreatedAt())
                                .build()
                ).toList())
                .upvoted(upvoteRepository.existsByUserIdAndPostId(user.getId(), postId))
                .build();
    }


}

