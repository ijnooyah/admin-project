package com.yoonji.adminproject.user.service;


import com.yoonji.adminproject.common.exception.CustomException;
import com.yoonji.adminproject.common.exception.ErrorCode;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collections;
import java.util.Optional;
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

    private Role role;


    @BeforeEach
    void setUp() {
        role = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new CustomException(ErrorCode.ROLE_NOT_FOUND));
    }

    @Test
    @DisplayName("회원가입은 사용자를 생성하고 UserResponse를 반환해야 한다")
    void 회원가입_사용자생성_그리고_UserResponse_반환() {
        // given
        String email = "test@example.com";
        String nickname = "testUser";
        String password = "password";
        String picture = "picture";
        SignUpRequest request = new SignUpRequest();
        request.setEmail(email);
        request.setNickname(nickname);
        request.setPassword(password);
        request.setPicture(picture);

        // when
        UserResponse response = authService.signup(request);

        // then
        assertThat(response).isNotNull();
        assertThat(nickname).isEqualTo(response.getNickname());
        assertThat(email).isEqualTo(response.getEmail());
        assertThat(picture).isEqualTo(response.getPicture());

        // 저장된 사용자 확인
        User savedUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        assertThat(nickname).isEqualTo(savedUser.getNickname());
        assertThat(passwordEncoder.matches(password, savedUser.getPassword())).isTrue();
        Set<UserRole> expectedUserRoles = Collections.singleton(UserRole.createUserRole(this.role).updateUser(savedUser));
        assertThat(savedUser.getUserRoles())
                .hasSize(1)
                .containsExactlyInAnyOrderElementsOf(expectedUserRoles);
    }

    @Test
    @DisplayName("회원가입은 중복된 이메일일 경우 예외를 발생시켜야 한다")
    void 회원가입_중복된_이메일_예외발생() {
        // given
        SignUpRequest request = new SignUpRequest();
        request.setEmail("duplicate@example.com");
        request.setNickname("testUser");
        request.setPassword("password");

        // 먼저 사용자 등록
        authService.signup(request);

        // when & then (중복된 이메일로 다시 회원가입)
        assertThatThrownBy(() -> authService.signup(request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.EMAIL_ALREADY_EXISTS);
    }

}