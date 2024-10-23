package com.yoonji.adminproject.comment.repository;

import com.yoonji.adminproject.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {
    int countByPostIdAndDeletedFalseAndDeletedAtIsNull(Long postId);

    @Query("SELECT c FROM Comment c JOIN FETCH c.post p " +
            "WHERE c.id = :commentId " +
            "AND c.post.id = :postId " +
            "AND c.deleted = false " +
            "AND c.deletedAt IS NULL " +
            "AND p.deleted = false " +
            "AND p.deletedAt IS NULL")
    Optional<Comment> findByIdAndPostIdWithPost(Long commentId, Long postId);
}
