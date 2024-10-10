package com.yoonji.adminproject.user.controller;



import com.yoonji.adminproject.docs.user.controller.UserControllerDocs;
import com.yoonji.adminproject.common.dto.response.CommonResponse;
import com.yoonji.adminproject.user.dto.request.SignUpRequest;
import com.yoonji.adminproject.user.dto.request.UserPasswordRequest;
import com.yoonji.adminproject.user.dto.request.UserUpdateRequest;
import com.yoonji.adminproject.user.dto.response.UserResponse;
import com.yoonji.adminproject.security.principal.UserPrincipal;
import com.yoonji.adminproject.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController implements UserControllerDocs {

    private final UserService userService;

    @GetMapping("/me")
    public CommonResponse<UserResponse> getUser(@AuthenticationPrincipal UserPrincipal principal) {
        return new CommonResponse<>(HttpStatus.OK, userService.getUser(principal));
    }

    @GetMapping("/{id}")
    public CommonResponse<UserResponse> getUserById(@PathVariable Long id) {
        return new CommonResponse<>(HttpStatus.OK, userService.getUserById(id));
    }

    @PatchMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CommonResponse<UserResponse> updateUser(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestPart("userUpdateRequest") UserUpdateRequest request,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) throws IOException {
        return new CommonResponse<>(HttpStatus.OK, userService.updateUser(principal, request, profileImage));
    }

    @PatchMapping("/me/password")
    public CommonResponse<Void> changeUserPassword(@AuthenticationPrincipal UserPrincipal principal, @RequestBody UserPasswordRequest request) {
        userService.changeUserPassword(principal, request);
        return new CommonResponse<>(HttpStatus.OK);
    }

}
