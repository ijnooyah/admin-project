package com.yoonji.adminproject.comment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class CommentRequest {
    private String content;

    @Schema(hidden = true)
    private Long parentId;

    @Schema(hidden = true)
    private Long mentionedUser;
}
