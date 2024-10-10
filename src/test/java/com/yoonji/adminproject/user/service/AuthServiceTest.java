package com.yoonji.adminproject.user.service;


import com.yoonji.adminproject.common.exception.CustomException;
import com.yoonji.adminproject.common.exception.ErrorCode;
import com.yoonji.adminproject.file.service.FileService;
import com.yoonji.adminproject.user.dto.request.SignUpRequest;
import com.yoonji.adminproject.user.dto.response.UserResponse;
import com.yoonji.adminproject.user.entity.Role;
import com.yoonji.adminproject.user.entity.User;
import com.yoonji.adminproject.user.entity.UserRole;
import com.yoonji.adminproject.user.repository.RoleRepository;
import com.yoonji.adminproject.user.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("회원가입은 사용자를 생성하고 UserResponse를 반환해야 한다")
    void 회원가입_사용자생성_그리고_UserResponse_반환() throws IOException {
        // given
        String email = "test@example.com";
        String nickname = "testUser";
        String password = "password";
        SignUpRequest request = new SignUpRequest();
        request.setEmail(email);
        request.setNickname(nickname);
        request.setPassword(password);

        MockMultipartFile profileImage = new MockMultipartFile(
                "profileImage",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );

        // when
        UserResponse response = authService.signup(request, profileImage);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getNickname()).isEqualTo(nickname);
        assertThat(response.getEmail()).isEqualTo(email);
        assertThat(response.getProfileImageUrl()).isNotNull();

        // 저장된 사용자 확인
        User savedUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        assertThat(savedUser.getNickname()).isEqualTo(nickname);
        assertThat(passwordEncoder.matches(password, savedUser.getPassword())).isTrue();
        assertThat(savedUser.getProfileImage()).isNotNull();
        assertThat(savedUser.getUserRoles()).hasSize(1);
        assertThat(savedUser.getUserRoles().iterator().next().getRole().getName()).isEqualTo("ROLE_USER");
    }

    @Test
    @DisplayName("회원가입은 중복된 이메일일 경우 예외를 발생시켜야 한다")
    void 회원가입_중복된_이메일_예외발생() throws IOException {
        // given
        SignUpRequest request = new SignUpRequest();
        request.setEmail("duplicate@example.com");
        request.setNickname("testUser");
        request.setPassword("password");

        MockMultipartFile profileImage = new MockMultipartFile(
                "profileImage",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );

        // 먼저 사용자 등록
        authService.signup(request, profileImage);

        // when & then (중복된 이메일로 다시 회원가입)
        assertThatThrownBy(() -> authService.signup(request, profileImage))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.EMAIL_ALREADY_EXISTS);
    }

    @Test
    @DisplayName("회원가입은 프로필 이미지 없이도 가능해야 한다")
    void 회원가입_프로필이미지없이_가능() throws IOException {
        // given
        SignUpRequest request = new SignUpRequest();
        request.setEmail("noimage@example.com");
        request.setNickname("noImageUser");
        request.setPassword("password");

        // when
        UserResponse response = authService.signup(request, null);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getNickname()).isEqualTo("noImageUser");
        assertThat(response.getEmail()).isEqualTo("noimage@example.com");
        assertThat(response.getProfileImageUrl()).isNull();

        User savedUser = userRepository.findByEmail("noimage@example.com")
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        assertThat(savedUser.getProfileImage()).isNull();
    }
}