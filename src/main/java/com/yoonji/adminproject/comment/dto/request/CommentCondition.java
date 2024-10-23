package com.yoonji.adminproject.comment.dto.request;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CommentCondition {
    @Schema(description = "커서 ID ", example = "0")
    private String cursorId;

    @Schema(description = "사이즈", example = "10")
    private int size;

    @Schema(description = "정렬 기준", allowableValues = {"CREATED_AT"}, example = "CREATED_AT")
    private String sortType;

    @Schema(description = "정렬 방향", allowableValues = {"ASC", "DES"}, example = "ASC")
    private String order;

}
