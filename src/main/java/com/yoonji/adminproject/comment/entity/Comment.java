package com.yoonji.adminproject.comment.entity;

import com.yoonji.adminproject.comment.dto.request.CommentRequest;
import com.yoonji.adminproject.common.entity.BaseTimeEntity;
import com.yoonji.adminproject.file.entity.File;
import com.yoonji.adminproject.post.entity.Post;
import com.yoonji.adminproject.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.util.StringUtils.hasText;

@Entity
@Table(name = "comments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Comment extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent = null;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> replies = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentioned_user_id")
    private User mentionedUser;
    
    private int level = 0;

    private boolean deleted = false;

    private LocalDateTime deletedAt;

    // == 연관관계 메서드 ==
    public void addReply(Comment reply) {
        this.replies.add(reply);
        reply.updateParent(this);
    }

    // == 생성 메서드 ==
    public static <T extends CommentRequest> Comment createComment(T request, Post post, User author, Comment parent, User mentionedUser) {
        Comment comment = new Comment();
        comment.content = request.getContent();
        comment.post = post;
        comment.author = author;
        comment.parent = parent;
        if (parent != null) {
            parent.replies.add(comment);
            comment.level = 1;
        }
        comment.mentionedUser = mentionedUser;
        return comment;
    }

    // == 수정 메서드 ==

    public <T extends CommentRequest> void update(T request) {
        if (hasText(request.getContent())) {
            this.content = request.getContent();
        }
    }

    public void updatePost(Post post) {
        this.post = post;
    }

    public void updateParent(Comment parent) {
        this.parent = parent;
    }

    // == 삭제 메서드 ==
    public void delete() {
        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
    }
}