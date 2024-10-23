package com.yoonji.adminproject.post.service;


import com.yoonji.adminproject.comment.repository.CommentRepository;
import com.yoonji.adminproject.common.exception.CustomException;
import com.yoonji.adminproject.common.exception.ErrorCode;
import com.yoonji.adminproject.post.dto.request.PostRequest;
import com.yoonji.adminproject.post.dto.response.PostResponse;
import com.yoonji.adminproject.post.entity.Post;
import com.yoonji.adminproject.post.repository.PostRepository;
import com.yoonji.adminproject.security.principal.UserPrincipal;
import com.yoonji.adminproject.user.entity.User;
import com.yoonji.adminproject.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public PostResponse createPost(PostRequest request, UserPrincipal principal) {
        User user = userRepository.findByIdAndDeletedFalseAndDeletedAtIsNull(principal.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Post savedPost = postRepository.save(Post.createPost(request, user));

        return PostResponse.builder()
                .id(savedPost.getId())
                .title(savedPost.getTitle())
                .content(savedPost.getContent())
                .author(savedPost.getAuthor().getNickname())
                .createdAt(savedPost.getCreatedAt())
                .updatedAt(savedPost.getUpdatedAt())
                .commentCount(0)
                .build();
    }

    @Transactional(readOnly = true)
    public PostResponse getPost(Long postId) {
        Post findPost = postRepository.findByIdAndDeletedFalseAndDeletedAtIsNull(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        return PostResponse.builder()
                .id(findPost.getId())
                .title(findPost.getTitle())
                .content(findPost.getContent())
                .author(findPost.getAuthor().getNickname())
                .createdAt(findPost.getCreatedAt())
                .updatedAt(findPost.getUpdatedAt())
                .commentCount(commentRepository.countByPostIdAndDeletedFalseAndDeletedAtIsNull(postId))
                .build();
    }

    @Transactional
    public PostResponse updatePost(PostRequest request, UserPrincipal principal, Long postId) {
        Post findPost = postRepository.findByIdAndDeletedFalseAndDeletedAtIsNull(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        if (!findPost.getAuthor().getId().equals(principal.getId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        findPost.update(request);

        return PostResponse.builder()
                .id(findPost.getId())
                .title(findPost.getTitle())
                .content(findPost.getContent())
                .author(findPost.getAuthor().getNickname())
                .createdAt(findPost.getCreatedAt())
                .updatedAt(findPost.getUpdatedAt())
                .commentCount(commentRepository.countByPostIdAndDeletedFalseAndDeletedAtIsNull(postId))
                .build();
    }

    @Transactional
    public void deletePost(Long postId, UserPrincipal principal) {
        Post findPost = postRepository.findByIdAndDeletedFalseAndDeletedAtIsNull(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        if (!findPost.getAuthor().getId().equals(principal.getId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        findPost.delete();
    }


}
