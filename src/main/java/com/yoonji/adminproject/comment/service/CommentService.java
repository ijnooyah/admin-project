package com.yoonji.adminproject.comment.service;


import com.yoonji.adminproject.comment.dto.request.*;
import com.yoonji.adminproject.comment.dto.response.CommentListResponse;
import com.yoonji.adminproject.comment.dto.response.CommentResponse;
import com.yoonji.adminproject.comment.entity.Comment;
import com.yoonji.adminproject.comment.repository.CommentRepository;
import com.yoonji.adminproject.common.exception.CustomException;
import com.yoonji.adminproject.common.exception.ErrorCode;
import com.yoonji.adminproject.notification.entity.EntityType;
import com.yoonji.adminproject.notification.entity.NotificationType;
import com.yoonji.adminproject.notification.repository.NotificationRepository;
import com.yoonji.adminproject.notification.service.NotificationService;
import com.yoonji.adminproject.post.entity.Post;
import com.yoonji.adminproject.post.repository.PostRepository;
import com.yoonji.adminproject.security.principal.UserPrincipal;
import com.yoonji.adminproject.user.dto.response.UserResponse;
import com.yoonji.adminproject.user.entity.User;
import com.yoonji.adminproject.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Service
@RequiredArgsConstructor
public class CommentService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final NotificationService notificationService;

    @Transactional
    public CommentResponse createComment(CommentCreateRequest request, UserPrincipal principal, Long postId) {
        User user = userRepository.findByIdAndDeletedFalseAndDeletedAtIsNull(principal.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Comment parentComment = null;
        if (request.getParentId() != null && request.getParentId() != 0) {
            parentComment = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
        }

        User mentionedUser = null;
        if (request.getMentionedUser() != null && request.getMentionedUser() != 0) {
            mentionedUser = userRepository.findById(request.getMentionedUser())
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        }

        Post post = getPostByPostId(postId);
        Comment savedComment = commentRepository.save(
                Comment.createComment(
                        request,
                        post,
                        user,
                        parentComment,
                        mentionedUser
                )
        );

        notificationService.sendNotification(
                post.getAuthor(),
                user.getNickname() + "님이 댓글을 작성했습니다.",
                NotificationType.COMMENT,
                EntityType.POST,
                postId
        );

        return convertToCommentResponse(savedComment, mentionedUser);
    }

    private static CommentResponse convertToCommentResponse(Comment comment, User mentionedUser) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .author(comment.getAuthor().getNickname())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .mentionedUser(convertToUserResponse(mentionedUser))
                .parentId(comment.getParent() == null ? null : comment.getParent().getId())
                .level(comment.getLevel())
                .build();
    }

    private static UserResponse convertToUserResponse(User mentionedUser) {
        if (mentionedUser == null ) {
            return null;
        }

        return UserResponse.builder()
                .id(mentionedUser.getId())
                .nickname(mentionedUser.getNickname())
                .build();
    }

    @Transactional(readOnly = true)
    public CommentListResponse getComments(CommentCondition condition, Long postId) {
        getPostByPostId(postId);

        List<Comment> comments = commentRepository.getCommentsWithCursor(condition, postId);

        boolean hasNext = false;
        String nextCursorId = null;

        if (comments.size() > condition.getSize()) {
            hasNext = true;
            Long commentId = comments.getLast().getId();

            nextCursorId = switch (SortType.valueOf(condition.getSortType())) {
                case CREATED_AT -> generateCreatedAtCursorId(comments.getLast().getCreatedAt(), commentId);
            };
        }

        List<CommentResponse> commentResponses = comments.stream()
                .limit(condition.getSize())
                .map(comment -> convertToCommentResponse(comment, comment.getMentionedUser()))
                .toList();

        return CommentListResponse.builder()
                .comments(commentResponses)
                .hasNext(hasNext)
                .nextCursorId(nextCursorId)
                .build();
    }

    private String generateCreatedAtCursorId(LocalDateTime createdAt, Long commentId){
        // 1. LocalDateTime을 DATE_FORMAT와 동일한 포맷으로 변환 (yyMMddHHmmss)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMddHHmmss");
        String formattedCreatedAt = createdAt.format(formatter);

        // 2. 포맷된 createdAt을 20자리로 왼쪽을 '0'으로 채움
        String customCursorCreatedAt = StringUtils.leftPad(formattedCreatedAt, 20, '0');

        // 3. userId를 문자열로 변환하고 10자리로 왼쪽을 '0'으로 채움
        String customCursorId = StringUtils.leftPad(commentId.toString(), 10, '0');

        // 4. 두 값을 연결하여 반환
        return customCursorCreatedAt + customCursorId;
    }

    @Transactional
    public CommentResponse updateComment(CommentUpdateRequest request, UserPrincipal principal, Long postId, Long commentId) {
        getPostByPostId(postId);

        Comment findComment = commentRepository.findByIdAndPostIdWithPost(commentId, postId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        if (!findComment.getAuthor().getId().equals(principal.getId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        findComment.update(request);

        return convertToCommentResponse(findComment, findComment.getMentionedUser());
    }

    @Transactional
    public void deleteComment(Long postId, Long commentId, UserPrincipal principal) {
        getPostByPostId(postId);

        Comment findComment = commentRepository.findByIdAndPostIdWithPost(commentId, postId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        if (!findComment.getAuthor().getId().equals(principal.getId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        findComment.delete();
    }

    private Post getPostByPostId(Long postId) {
        return postRepository.findByIdAndDeletedFalseAndDeletedAtIsNull(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
    }

}
