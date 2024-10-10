package com.yoonji.adminproject.user.controller;

import com.yoonji.adminproject.common.dto.response.CommonResponse;
import com.yoonji.adminproject.docs.user.controller.AuthControllerDocs;
import com.yoonji.adminproject.security.principal.UserPrincipal;
import com.yoonji.adminproject.security.token.RestAuthenticationToken;
import com.yoonji.adminproject.user.dto.request.AdditionalInfoRequest;
import com.yoonji.adminproject.user.dto.request.SignUpRequest;
import com.yoonji.adminproject.user.dto.response.UserResponse;
import com.yoonji.adminproject.user.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController implements AuthControllerDocs {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final HttpSessionSecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();


    @PostMapping(value = "/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CommonResponse<UserResponse> signup(
            @RequestPart("signUpRequest") SignUpRequest signUpRequest,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        UserResponse signup = authService.signup(signUpRequest, profileImage);

        // 회원 가입 후 자동 로그인
        Authentication authentication = authenticationManager.authenticate(
                new RestAuthenticationToken(signUpRequest.getEmail(), signUpRequest.getPassword())
        );
        SecurityContext securityContext = SecurityContextHolder.getContextHolderStrategy().createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.getContextHolderStrategy().setContext(securityContext);

        securityContextRepository.saveContext(securityContext, request, response);
        return new CommonResponse<>(HttpStatus.OK, signup);
    }

    @PostMapping(value = "/logout")
    public CommonResponse<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContextHolderStrategy().getContext().getAuthentication();
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        return new CommonResponse<>(HttpStatus.OK);
    }

    @PatchMapping(value = "/additional-info", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CommonResponse<UserResponse> updateUser(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestPart("additionalInfoRequest") AdditionalInfoRequest request,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) throws IOException {
        return new CommonResponse<>(HttpStatus.OK, authService.updateUser(principal, request, profileImage));
    }
}
