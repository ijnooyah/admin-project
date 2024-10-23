package com.yoonji.adminproject.comment.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CommentListResponse {

    private List<CommentResponse> comments;
    private String nextCursorId;
    private boolean hasNext;

    @Builder
    public CommentListResponse(List<CommentResponse> comments, String nextCursorId, boolean hasNext) {
        this.comments = comments;
        this.nextCursorId = nextCursorId;
        this.hasNext = hasNext;
    }
}
