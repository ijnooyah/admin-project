package com.yoonji.adminproject.admin.controller;


import com.yoonji.adminproject.admin.dto.request.AdminUserAddRequest;
import com.yoonji.adminproject.admin.dto.request.AdminUserRolesRequest;
import com.yoonji.adminproject.admin.dto.request.AdminUserUpdateRequest;
import com.yoonji.adminproject.admin.dto.response.AdminUserListResponse;
import com.yoonji.adminproject.admin.dto.response.AdminUserResponse;
import com.yoonji.adminproject.admin.service.AdminUserService;
import com.yoonji.adminproject.common.dto.response.CommonResponse;
import com.yoonji.adminproject.docs.admin.controller.AdminUserControllerDocs;
import com.yoonji.adminproject.admin.dto.request.AdminUserCursorRequest;
import com.yoonji.adminproject.user.dto.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class AdminUserController implements AdminUserControllerDocs {

    private final AdminUserService adminUserService;

    @GetMapping("/cursor")
    public CommonResponse<AdminUserListResponse> getUsersWithCursor(
            @RequestParam(required = false) Long cursorId,
            @RequestParam(defaultValue = "10") int size) {
        return new CommonResponse<>(HttpStatus.OK, adminUserService.getUsersWithCursor(cursorId, size));
    }

    @DeleteMapping("/{id}")
    public CommonResponse<Void> deleteUser(@PathVariable Long id) {
        adminUserService.deleteUser(id);
        return new CommonResponse<>(HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public CommonResponse<AdminUserResponse> updateUser(@PathVariable Long id, @RequestBody AdminUserUpdateRequest request) {
        return new CommonResponse<>(HttpStatus.OK, adminUserService.updateUser(id, request));
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
