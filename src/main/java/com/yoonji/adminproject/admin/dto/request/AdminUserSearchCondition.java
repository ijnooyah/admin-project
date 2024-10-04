package com.yoonji.adminproject.admin.dto.request;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AdminUserSearchCondition {
    @Schema(description = "커서 ID ", example = "0")
    private String cursorId;

    @Schema(description = "사이즈", example = "10")
    private int size;

    @Schema(description = "서치 내용", example = "example")
    private String searchInput;

    @Schema(description = "서치 타입", allowableValues = {"ALL", "NICKNAME", "EMAIL"}, example = "ALL")
    private String searchType;

    @ArraySchema(schema = @Schema(type = "string", allowableValues = {"ROLE_USER", "ROLE_ADMIN", "ROLE_MANAGER"}))
    private List<String> roles;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "가입일 범위 시작", example = "2024-09-01 00:00:00")
    private LocalDateTime startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "가입일 범위 끝", example = "2024-10-01 00:00:00")
    private LocalDateTime endDate;

    @ArraySchema(schema = @Schema(type = "string", allowableValues = {"GOOGLE", "NAVER", "LOCAL"}))
    private List<String> providers;

    @Schema(description = "정렬 기준", allowableValues = {"EMAIL", "CREATED_AT"}, example = "CREATED_AT")
    private String sortType;

    @Schema(description = "정렬 방향", allowableValues = {"ASC", "DES"}, example = "ASC")
    private String order;

}
