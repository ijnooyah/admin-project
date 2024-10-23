package com.yoonji.adminproject.post.repository;

import com.yoonji.adminproject.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findByIdAndDeletedFalseAndDeletedAtIsNull(Long id);
}
