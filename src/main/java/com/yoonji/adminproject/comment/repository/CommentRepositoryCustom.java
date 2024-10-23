package com.yoonji.adminproject.comment.repository;

import com.yoonji.adminproject.comment.dto.request.CommentCondition;
import com.yoonji.adminproject.comment.entity.Comment;

import java.util.List;


public interface CommentRepositoryCustom {
    List<Comment> getCommentsWithCursor(CommentCondition condition , Long postId);
}
