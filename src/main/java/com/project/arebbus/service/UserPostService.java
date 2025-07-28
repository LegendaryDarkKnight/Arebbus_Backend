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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final CommentUpvoteRepository commentUpvoteRepository;

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


//    public PagedPostResponse getAllPostsPage(User user, int page, int size) {
//        Page<Post> posts = postRepository.findAll(PageRequest.of(page, size));
//
//        LOGGER.debug("Total posts found: {}", posts.getTotalElements());
//
//
//        List<PostSummaryResponse> postSummaries = posts.getContent().stream().map(
//                post -> PostSummaryResponse.builder()
//                        .postId(post.getId())
//                        .authorName(post.getAuthor().getName())
//                        .content(post.getContent())
//                        .numUpvote(post.getNumUpvote())
//                        .createdAt(post.getCreatedAt())
//                        .tags(tagRepository.findTagsByPostId(post.getId()).stream()
//                                .map(Tag::getName)
//                                .toList())
//                        .upvoted(upvoteRepository.existsByUserIdAndPostId(user.getId(), post.getId()))
//                        .build()
//        ).toList();
//
//
//        return PagedPostResponse.builder()
//                .posts(postSummaries)
//                .page(posts.getNumber())
//                .size(posts.getSize())
//                .totalPages(posts.getTotalPages())
//                .totalElements(posts.getTotalElements())
//                .build();
//    }

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
        Page<Post> posts = postRepository.findByAuthor(user, PageRequest.of(page, size));

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

    public PagedPostResponse getAllPostsPage(User user, int page, int size) {
        Page<Post> posts = postRepository.findAll(PageRequest.of(page, size));
        return buildPagedResponse(posts, user);
    }

    public PagedPostResponse getFilteredPostsPage(User user, PostFilterDTO filter) {
        Page<Post> posts;
        Pageable pageable = filter.toPageable();

        // Check if any filters are applied
        if (hasFilters(filter)) {
            // Handle tag filtering logic
            if (filter.getTags() != null && !filter.getTags().isEmpty()) {
                if (Boolean.TRUE.equals(filter.getTagsMatchAll())) {
                    // Use method for ALL tags matching
                    posts = postRepository.findPostsByAllTagNames(
                            filter.getTags(),
                            (long) filter.getTags().size(),
                            pageable
                    );
                } else {
                    // Use method for ANY tags matching
                    posts = postRepository.findPostsByAnyTagNames(filter.getTags(), pageable);
                }
            } else {
                // Use complex filter query for non-tag filters
                posts = postRepository.findPostsWithFilters(
                        filter.getContent(),
                        filter.getAuthorId(),
                        filter.getTags(),
                        filter.getMinUpvotes(),
                        filter.getFromDate(),
                        filter.getToDate(),
                        pageable
                );
            }
        } else {
            // No filters, return all posts
            posts = postRepository.findAll(pageable);
        }

        LOGGER.debug("Total posts found with filters: {}", posts.getTotalElements());
        return buildPagedResponse(posts, user);
    }

    // Enhanced hasFilters method
    private boolean hasFilters(PostFilterDTO filter) {
        return (filter.getContent() != null && !filter.getContent().trim().isEmpty()) ||
                (filter.getTags() != null && !filter.getTags().isEmpty()) ||
                filter.getAuthorId() != null ||
                filter.getMinUpvotes() != null ||
                filter.getFromDate() != null ||
                filter.getToDate() != null;
    }

    // Alternative method for specific filter types
    public PagedPostResponse getPostsByTag(User user, String tagName, int page, int size, String sortBy) {
        Pageable pageable = createPageable(page, size, sortBy);
        Page<Post> posts = postRepository.findPostsByTagName(tagName, pageable);
        return buildPagedResponse(posts, user);
    }

    public PagedPostResponse getPostsByContent(User user, String content, int page, int size, String sortBy) {
        Pageable pageable = createPageable(page, size, sortBy);
        Page<Post> posts = postRepository.findByContentContainingIgnoreCase(content, pageable);
        return buildPagedResponse(posts, user);
    }

    public PagedPostResponse getRecentPosts(User user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Post> posts = postRepository.findAll(pageable);
        return buildPagedResponse(posts, user);
    }

    public PagedPostResponse getPopularPosts(User user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "numUpvote"));
        Page<Post> posts = postRepository.findAll(pageable);
        return buildPagedResponse(posts, user);
    }

    private PagedPostResponse buildPagedResponse(Page<Post> posts, User user) {
        List<PostSummaryResponse> postSummaries = posts.getContent().stream()
                .map(post -> PostSummaryResponse.builder()
                        .postId(post.getId())
                        .authorName(post.getAuthor().getName())
                        .content(post.getContent())
                        .numUpvote(post.getNumUpvote())
                        .createdAt(post.getCreatedAt())
                        .tags(tagRepository.findTagsByPostId(post.getId()).stream()
                                .map(Tag::getName)
                                .toList())
                        .upvoted(upvoteRepository.existsByUserIdAndPostId(user.getId(), post.getId()))
                        .build())
                .toList();

        return PagedPostResponse.builder()
                .posts(postSummaries)
                .page(posts.getNumber())
                .size(posts.getSize())
                .totalPages(posts.getTotalPages())
                .totalElements(posts.getTotalElements())
                .build();
    }

//    private boolean hasFilters(PostFilterDTO filter) {
//        return filter.getContent() != null ||
//                (filter.getTags() != null && !filter.getTags().isEmpty()) ||
//                filter.getAuthorId() != null ||
//                filter.getMinUpvotes() != null ||
//                filter.getFromDate() != null ||
//                filter.getToDate() != null;
//    }

    private Pageable createPageable(int page, int size, String sortBy) {
        Sort sort = switch (sortBy != null ? sortBy.toLowerCase() : "recent") {
            case "popular" -> Sort.by(Sort.Direction.DESC, "num_upvote");
            case "oldest" -> Sort.by(Sort.Direction.ASC, "created_at");
            default -> Sort.by(Sort.Direction.DESC, "created_at");
        };
        return PageRequest.of(page, size, sort);
    }
}
