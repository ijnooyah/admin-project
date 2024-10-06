package com.yoonji.adminproject.user.controller;

import com.yoonji.adminproject.common.dto.response.CommonResponse;
import com.yoonji.adminproject.docs.user.controller.AuthControllerDocs;
import com.yoonji.adminproject.security.token.RestAuthenticationToken;
import com.yoonji.adminproject.user.dto.request.SignUpRequest;
import com.yoonji.adminproject.user.dto.response.UserResponse;
import com.yoonji.adminproject.user.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController implements AuthControllerDocs {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final HttpSessionSecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();


    @PostMapping("/signup")
    public CommonResponse<UserResponse> signup(@RequestBody SignUpRequest signUpRequest, HttpServletRequest request, HttpServletResponse response) {

        UserResponse signup = authService.signup(signUpRequest);

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
}
