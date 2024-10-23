package com.yoonji.adminproject.post.entity;

import com.yoonji.adminproject.comment.entity.Comment;
import com.yoonji.adminproject.common.entity.BaseTimeEntity;
import com.yoonji.adminproject.post.dto.request.PostRequest;
import com.yoonji.adminproject.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Post extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User author;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    private boolean deleted = false;

    private LocalDateTime deletedAt;

    // == 연관관계 메서드 ==
    public void addComment(Comment comment) {
        this.comments.add(comment);
        comment.updatePost(this);
    }

    // == 생성 메서드 ==
    public static Post createPost(PostRequest request, User author) {
        Post post = new Post();
        post.title = request.getTitle();
        post.content = request.getContent();
        post.author = author;
        return post;
    }

    // == 수정 메서드 ==
    public void update(PostRequest request) {
        this.title = request.getTitle();
        this.content = request.getContent();
    }

    // == 삭제 메서드 ==
    public void delete() {
        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
    }
}