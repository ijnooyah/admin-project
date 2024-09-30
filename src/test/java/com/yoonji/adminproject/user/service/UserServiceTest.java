package com.yoonji.adminproject.user.service;

import com.yoonji.adminproject.common.exception.CustomException;
import com.yoonji.adminproject.common.exception.ErrorCode;
import com.yoonji.adminproject.security.principal.UserPrincipal;
import com.yoonji.adminproject.user.dto.request.SignUpRequest;
import com.yoonji.adminproject.user.dto.request.UserPasswordRequest;
import com.yoonji.adminproject.user.dto.request.UserUpdateRequest;
import com.yoonji.adminproject.user.dto.response.UserResponse;
import com.yoonji.adminproject.user.entity.Role;
import com.yoonji.adminproject.user.entity.User;
import com.yoonji.adminproject.user.entity.UserRole;
import com.yoonji.adminproject.user.repository.RoleRepository;
import com.yoonji.adminproject.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User user;

    @BeforeEach
    public void setUp() {
        Role role = roleRepository.findByName("ROLE_USER").orElseThrow(() -> new CustomException(ErrorCode.ROLE_NOT_FOUND));

        SignUpRequest request = new SignUpRequest();
        request.setEmail("test@example.com");
        request.setNickname("nickname");
        request.setPicture("picture");
        request.setPassword("password");

        UserRole userRole = UserRole.createUserRole(role);

        User createdUser = User.createLocalUser(request, passwordEncoder, Collections.singleton(userRole));

        // 사용자 저장
        user = userRepository.save(createdUser);
    }

    @Test
    @DisplayName("getUser: 사용자 정보를 올바르게 반환해야 한다")
    void getUser_사용자_정보_반환() {
        // given
        UserPrincipal principal = new UserPrincipal(user, null);

        // when
        UserResponse response = userService.getUser(principal);

        // then
        assertThat(response).isNotNull()
                .extracting(UserResponse::getNickname, UserResponse::getEmail, UserResponse::getPicture)
                .containsExactly(user.getNickname(), user.getEmail(), user.getPicture());
    }

    @Test
    @DisplayName("getUserById: 사용자 정보를 올바르게 반환해야 한다")
    void getUserBy_사용자_정보_반환() {
        // given
        Long id = user.getId();

        // when
        UserResponse response = userService.getUserById(id);

        // then
        assertThat(response).isNotNull()
                .extracting(UserResponse::getNickname, UserResponse::getEmail, UserResponse::getPicture)
                .containsExactly(user.getNickname(), user.getEmail(), user.getPicture());
    }

    @Test
    @DisplayName("updateUser: 사용자 정보를 올바르게 수정해야 한다")
    void updateUser_사용자_정보_수정() {
        // given
        UserPrincipal principal = new UserPrincipal(user, null);
        String newNickname = "newNickname";
        String newPicture = "newPicture";
        UserUpdateRequest request = new UserUpdateRequest(newNickname, newPicture);

        // when
        UserResponse response = userService.updateUser(principal, request);

        // then
        assertThat(response).extracting(UserResponse::getNickname, UserResponse::getPicture)
                .containsExactly(newNickname, newPicture);

        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updatedUser).extracting(User::getNickname, User::getPicture)
                .containsExactly(newNickname, newPicture);
    }

    @Test
    @DisplayName("changeUserPassword: 사용자 비밀번호를 올바르게 변경해야 한다")
    void changeUserPassword_사용자_비밀번호_변경() {
        // given
        UserPrincipal principal = new UserPrincipal(user, null);
        String newPassword = "newPassword";
        UserPasswordRequest request = new UserPasswordRequest();
        request.setNewPassword(newPassword);

        // when
        userService.changeUserPassword(principal, request);

        // then
        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(passwordEncoder.matches(newPassword, updatedUser.getPassword())).isTrue();
    }

}