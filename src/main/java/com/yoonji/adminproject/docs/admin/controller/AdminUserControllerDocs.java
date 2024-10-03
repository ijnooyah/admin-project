package com.yoonji.adminproject.docs.admin.controller;

import com.yoonji.adminproject.admin.dto.request.AdminUserAddRequest;
import com.yoonji.adminproject.admin.dto.request.AdminUserRolesRequest;
import com.yoonji.adminproject.admin.dto.request.AdminUserUpdateRequest;
import com.yoonji.adminproject.admin.dto.response.AdminUserListResponse;
import com.yoonji.adminproject.admin.dto.response.AdminUserResponse;
import com.yoonji.adminproject.common.dto.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Admin User", description = "Admin User API")
public interface AdminUserControllerDocs {

    @Operation(summary = "회원 커서 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 커서 조회 성공"),
    })
    CommonResponse<AdminUserListResponse> getUsersWithCursor(
            @RequestParam(required = false) Long cursorId,
            @RequestParam(defaultValue = "10") int size);

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
