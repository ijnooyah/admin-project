package com.yoonji.adminproject.comment.dto.response;

import com.yoonji.adminproject.user.dto.response.UserResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CommentResponse {
    private Long id;
    private String content;
    private String author;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserResponse mentionedUser;  // 멘션된 사용자
    private Long parentId;         // 부모 댓글 ID
    private List<CommentResponse> replies = new ArrayList<>();  // 대댓글 리스트
    private int level;             // 댓글 깊이 레벨 (0: 원댓글, 1: 대댓글)

    @Builder
    public CommentResponse(Long id, String content, String author,
                           LocalDateTime createdAt, LocalDateTime updatedAt,
                           UserResponse mentionedUser, Long parentId, int level, List<CommentResponse> replies) {
        this.id = id;
        this.content = content;
        this.author = author;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.mentionedUser = mentionedUser;
        this.parentId = parentId;
        this.level = level;
        this.replies = replies;
    }

    public void addReply(CommentResponse reply) {
        this.replies.add(reply);
    }
}
