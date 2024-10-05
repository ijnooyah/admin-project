package com.yoonji.adminproject.docs.admin.controller;

import com.yoonji.adminproject.admin.dto.request.AdminUserAddRequest;
import com.yoonji.adminproject.admin.dto.request.AdminUserRolesRequest;
import com.yoonji.adminproject.admin.dto.request.AdminUserSearchCondition;
import com.yoonji.adminproject.admin.dto.request.AdminUserUpdateRequest;
import com.yoonji.adminproject.admin.dto.response.AdminUserListResponse;
import com.yoonji.adminproject.admin.dto.response.AdminUserResponse;
import com.yoonji.adminproject.admin.dto.response.NewUserStatisticsResponse;
import com.yoonji.adminproject.common.dto.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Tag(name = "Admin User", description = "Admin User API")
public interface AdminUserControllerDocs {

    @Operation(summary = "신규 가입자 수 통계 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "신규 가입자 수 통계 조회 성공"),
    })
    CommonResponse<NewUserStatisticsResponse> getNewUserStatistics(@RequestParam @Schema(description = "일간, 주간, 월간, 연간", allowableValues = {"day", "week", "month", "email"}, example = "day") String timeUnit,
                                                                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @Schema(description = "범위 시작", example = "2024-09-01") LocalDate startDate,
                                                                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @Schema(description = "범위 끝", example = "2024-10-05") LocalDate endDate);


    @Operation(summary = "회원 검색")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 검색 성공"),
    })
    CommonResponse<AdminUserListResponse> searchUsersWithCursor(@ParameterObject AdminUserSearchCondition condition);

    @Operation(summary = "회원 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 삭제 성공"),
    })
    CommonResponse<Void> deleteUser(@PathVariable Long id);

    @Operation(summary = "회원 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 수정 성공"),
    })
    CommonResponse<AdminUserResponse> updateUser(@PathVariable Long id, @RequestBody AdminUserUpdateRequest request);

    @Operation(summary = "회원 권한 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 권한 수정 성공"),
    })
    CommonResponse<AdminUserResponse> updateUserRoles(@PathVariable Long id, @RequestBody AdminUserRolesRequest request);

    @Operation(summary = "회원 추가")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 추가 성공"),
    })
    CommonResponse<AdminUserResponse> addUser(@RequestBody AdminUserAddRequest request);
}
