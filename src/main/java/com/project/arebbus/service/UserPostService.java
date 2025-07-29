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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserPostService {

    /** Repository for  data access */
    private final PostRepository postRepository;
    /** Repository for  data access */
    private final TagRepository tagRepository;
    /** Repository for  data access */
    private final PostTagRepository postTagRepository;
    /** Repository for  data access */
    private final CommentRepository commentRepository;
    /** Repository for  data access */
    private final UpvoteRepository upvoteRepository;
    /** Repository for  data access */
    private final CommentUpvoteRepository commentUpvoteRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserPostService.class);


    /**
     * Creates a new .
     * 
     * @param user The user creating the 
     * @param request The creation request
     * @return UserPostCreateResponse containing the created 
     */
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


    /**
     * Deletes a .
     * 
     * @param user The user deleting the 
     * @param request The deletion request
     * @return UserPostDeleteResponse containing deletion status
     */
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


    /**
     * Retrieves all  with pagination.
     * 
     * @param user The user requesting 
     * @param page The page number
     * @param size The page size
     * @return PagedPostResponse containing  and pagination info
     */
    public PagedPostResponse getAllPostsPage(User user, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt").and(Sort.by(Sort.Direction.DESC, "id"));
        Page<Post> posts = postRepository.findAll(PageRequest.of(page, size, sort));

        LOGGER.debug("Total posts found: {}", posts.getTotalElements());


        List<PostSummaryResponse> postSummaries = posts.getContent().stream().map(
                post -> PostSummaryResponse.builder()
                        .postId(post.getId())
                        .authorName(post.getAuthor().getName())
                        .content(post.getContent())
                        .numUpvote(post.getNumUpvote())
                        .createdAt(post.getCreatedAt())
                        .tags(tagRepository.findTagsByPostId(post.getId()).stream()
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

    /**
     * Retrieves a  by its ID.
     * 
     * @param id The ID of the  to retrieve
     * @param user The user requesting the 
     * @return PostResponse containing  details
     */
    public PostResponse getPostById(User user, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        return PostResponse.builder()
                .postId(post.getId())
                .authorName(post.getAuthor().getName())
                .content(post.getContent())
                .numUpvote(post.getNumUpvote())
                .createdAt(post.getCreatedAt())
                .tags(tagRepository.findTagsByPostId(postId).stream()
                        .map(Tag::getName)
                        .toList())
                .comments(commentRepository.findByPost(post).stream().map(
                        comment -> CommentResponse.builder()
                                .id(comment.getId())
                                .content(comment.getContent())
                                .authorName(comment.getAuthor().getName())
                                .postId(postId)
                                .createdAt(comment.getCreatedAt())
                                .numUpvote(comment.getNumUpvote())
                                .upvoted(commentUpvoteRepository.existsByUserIdAndCommentId(user.getId(), comment.getId()))
                                .build()
                ).toList())
                .upvoted(upvoteRepository.existsByUserIdAndPostId(user.getId(), postId))
                .build();
    }


    public PagedPostResponse getMyPostsPage(User user, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt").and(Sort.by(Sort.Direction.DESC, "id"));
        Page<Post> posts = postRepository.findByAuthor(user, PageRequest.of(page, size, sort));

        LOGGER.debug("Total posts found for user {}: {}", user.getId(), posts.getTotalElements());

        List<PostSummaryResponse> postSummaries = posts.getContent().stream().map(
                post -> PostSummaryResponse.builder()
                        .postId(post.getId())
                        .authorName(post.getAuthor().getName())
                        .content(post.getContent())
                        .numUpvote(post.getNumUpvote())
                        .createdAt(post.getCreatedAt())
                        .tags(tagRepository.findTagsByPostId(post.getId()).stream()
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

    public PagedPostResponse getPostsByTags(User user, List<String> tagNames, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt").and(Sort.by(Sort.Direction.DESC, "id"));
        Page<Post> posts = postRepository.findByTagNames(tagNames, PageRequest.of(page, size, sort));

        List<PostSummaryResponse> postSummaries = posts.getContent().stream().map(
                post -> PostSummaryResponse.builder()
                        .postId(post.getId())
                        .authorName(post.getAuthor().getName())
                        .content(post.getContent())
                        .numUpvote(post.getNumUpvote())
                        .createdAt(post.getCreatedAt())
                        .tags(tagRepository.findTagsByPostId(post.getId()).stream()
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

    public List<String> getAllTags() {
        return tagRepository.findAll().stream()
                .map(Tag::getName)
                .sorted()
                .toList();
    }
}

