package com.yoonji.adminproject.admin.controller;


import com.yoonji.adminproject.admin.dto.request.AdminUserAddRequest;
import com.yoonji.adminproject.admin.dto.request.AdminUserRolesRequest;
import com.yoonji.adminproject.admin.dto.request.AdminUserSearchCondition;
import com.yoonji.adminproject.admin.dto.request.AdminUserUpdateRequest;
import com.yoonji.adminproject.admin.dto.response.AdminUserListResponse;
import com.yoonji.adminproject.admin.dto.response.AdminUserResponse;
import com.yoonji.adminproject.admin.dto.response.NewUserStatisticsResponse;
import com.yoonji.adminproject.admin.service.AdminUserService;
import com.yoonji.adminproject.common.dto.response.CommonResponse;
import com.yoonji.adminproject.docs.admin.controller.AdminUserControllerDocs;
import com.yoonji.adminproject.user.dto.request.UserUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class AdminUserController implements AdminUserControllerDocs {

    private final AdminUserService adminUserService;

    @GetMapping("/stats/new")
    public CommonResponse<NewUserStatisticsResponse> getNewUserStatistics(@RequestParam String timeUnit,
                                                                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return new CommonResponse<>(HttpStatus.OK, adminUserService.getNewUserStatistics(timeUnit, startDate, endDate));
    }

    @GetMapping("/search")
    public CommonResponse<AdminUserListResponse> searchUsersWithCursor(@ParameterObject AdminUserSearchCondition condition) {
        return new CommonResponse<>(HttpStatus.OK, adminUserService.searchUsersWithCursor(condition));
    }

    @DeleteMapping("/{id}")
    public CommonResponse<Void> deleteUser(@PathVariable Long id) {
        adminUserService.deleteUser(id);
        return new CommonResponse<>(HttpStatus.OK);
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CommonResponse<AdminUserResponse> updateUser(
            @PathVariable Long id,
            @RequestPart("adminUserUpdateRequest") AdminUserUpdateRequest request,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage
    ) throws IOException {
        return new CommonResponse<>(HttpStatus.OK, adminUserService.updateUser(id, request, profileImage));
    }

    @PatchMapping("/{id}/roles")
    public CommonResponse<AdminUserResponse> updateUserRoles(@PathVariable Long id, @RequestBody AdminUserRolesRequest request) {
        return new CommonResponse<>(HttpStatus.OK, adminUserService.updateUserRoles(id, request));
    }

    @PostMapping
    public CommonResponse<AdminUserResponse> addUser(@RequestBody AdminUserAddRequest request) {
        return new CommonResponse<>(HttpStatus.OK, adminUserService.addUser(request));
    }

}
