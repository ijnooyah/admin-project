package com.yoonji.adminproject.comment.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentUpdateRequest extends CommentRequest {
    @NotBlank(message = "댓글 내용은 필수 입력 항목입니다.")
    private String content;
}
