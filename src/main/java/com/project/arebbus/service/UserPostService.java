package com.project.arebbus.service;

import com.project.arebbus.dto.*;
import com.project.arebbus.exception.PostNotFoundException;
import com.project.arebbus.exception.UnauthorizedPostAccessException;
import com.project.arebbus.model.Post;
import com.project.arebbus.model.PostTag;
import com.project.arebbus.model.Tag;
import com.project.arebbus.model.User;
import com.project.arebbus.repositories.CommentRepository;
import com.project.arebbus.repositories.PostRepository;
import com.project.arebbus.repositories.PostTagRepository;
import com.project.arebbus.repositories.TagRepository;
import lombok.RequiredArgsConstructor;
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


    public UserPostCreateResponse createPost(User user, String content, List<String> tagNames) {
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


    public UserPostDeleteResponse deletePost(User user, Long postId) {
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


    public PagedPostResponse getAllPostsPage(int page, int size) {
        Page<Post> posts = postRepository.findAll(PageRequest.of(page, size));

        System.out.println("Total posts found: " + posts.getTotalElements());


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

    public PostResponse getPostById(Long postId) {
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
                .build();
    }


}

