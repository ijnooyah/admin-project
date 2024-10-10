package com.yoonji.adminproject.docs.user.controller;

import com.yoonji.adminproject.common.dto.response.CommonResponse;
import com.yoonji.adminproject.common.exception.ErrorResponse;
import com.yoonji.adminproject.security.principal.UserPrincipal;
import com.yoonji.adminproject.user.dto.request.AdditionalInfoRequest;
import com.yoonji.adminproject.user.dto.request.SignUpRequest;
import com.yoonji.adminproject.user.dto.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Tag(name = "Auth", description = "Auth API")
public interface AuthControllerDocs {

    @Operation(summary = "회원가입")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @ApiResponse(responseCode = "409", description = "회원가입 실패(이미 존재하는 이메일)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    CommonResponse<UserResponse> signup(
            @RequestPart("signUpRequest") SignUpRequest signUpRequest,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException;

    @Operation(summary = "로그아웃")
    @ApiResponse(responseCode = "200", description = "로그아웃 성공")
    CommonResponse<Void> logout(HttpServletRequest request, HttpServletResponse response);

    @Operation(summary = "소셜 로그인으로 회원가입 후 추가 정보 입력")
    @ApiResponse(responseCode = "200", description = "성공")
    CommonResponse<UserResponse> updateUser(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestPart("additionalInfoRequest") AdditionalInfoRequest request,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) throws IOException;
}
