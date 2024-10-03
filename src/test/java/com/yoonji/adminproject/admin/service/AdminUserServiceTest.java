package com.yoonji.adminproject.admin.service;

import com.yoonji.adminproject.admin.dto.request.AdminUserAddRequest;
import com.yoonji.adminproject.admin.dto.request.AdminUserRolesRequest;
import com.yoonji.adminproject.admin.dto.request.AdminUserUpdateRequest;
import com.yoonji.adminproject.admin.dto.response.AdminUserListResponse;
import com.yoonji.adminproject.admin.dto.response.AdminUserResponse;
import com.yoonji.adminproject.common.exception.CustomException;
import com.yoonji.adminproject.common.exception.ErrorCode;
import com.yoonji.adminproject.user.dto.request.SignUpRequest;
import com.yoonji.adminproject.user.entity.ProviderType;
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
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class AdminUserServiceTest {

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User user;

    @BeforeEach
    void setUp() {
        Role role = roleRepository.findByName("ROLE_USER").orElseThrow(() -> new CustomException(ErrorCode.ROLE_NOT_FOUND));

        SignUpRequest request = new SignUpRequest();
        request.setEmail("test@example.com");
        request.setNickname("nickname");
        request.setPicture("picture");
        request.setPassword("password");

        UserRole userRole = UserRole.createUserRole(role);

        User createdUser = User.createLocalUser(request, passwordEncoder, Collections.singleton(userRole));

        user = userRepository.save(createdUser);
    }

    @Test
    @DisplayName("사용자 삭제")
    void deleteUser() {
        adminUserService.deleteUser(user.getId());

        assertThat(userRepository.findById(user.getId()))
                .isPresent()
                .get()
                .extracting(User::isDeleted)
                .isEqualTo(true);
    }

    @Test
    @DisplayName("사용자 정보 수정")
    void updateUser() {
        AdminUserUpdateRequest request = new AdminUserUpdateRequest("NewNickname", "new_picture.jpg");

        AdminUserResponse response = adminUserService.updateUser(user.getId(), request);

        assertThat(response.getNickname()).isEqualTo("NewNickname");
        assertThat(response.getPicture()).isEqualTo("new_picture.jpg");
    }

    @Test
    @DisplayName("사용자 역할 수정")
    void updateUserRoles() {
        AdminUserRolesRequest request = new AdminUserRolesRequest(Set.of("ROLE_USER", "ROLE_ADMIN"));

        AdminUserResponse response = adminUserService.updateUserRoles(user.getId(), request);

        assertThat(response.getRoles()).containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");
    }

    @Test
    @DisplayName("새 사용자 추가")
    void addUser() {
        AdminUserAddRequest request = new AdminUserAddRequest("new@example.com", "NewUser", "password", "picture.jpg", Set.of("ROLE_USER"));

        AdminUserResponse response = adminUserService.addUser(request);

        assertThat(response.getEmail()).isEqualTo("new@example.com");
        assertThat(response.getNickname()).isEqualTo("NewUser");
        assertThat(response.getRoles()).containsExactly("ROLE_USER");
    }

    @Test
    @DisplayName("중복 이메일로 사용자 추가 시 예외 발생")
    void addUserWithDuplicateEmail() {
        AdminUserAddRequest request = new AdminUserAddRequest(user.getEmail(), "NewUser", "password", "picture.jpg", Set.of("ROLE_USER"));

        assertThatThrownBy(() -> adminUserService.addUser(request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.EMAIL_ALREADY_EXISTS);
    }

    @Test
    @DisplayName("존재하지 않는 사용자 정보 수정 시 예외 발생")
    void updateNonExistentUser() {
        AdminUserUpdateRequest request = new AdminUserUpdateRequest("NewNickname", "new_picture.jpg");

        assertThatThrownBy(() -> adminUserService.updateUser(999L, request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("커서 기반으로 사용자 조회")
    void getUsersWithCursor() {

        AdminUserListResponse firstPageResponse = adminUserService.getUsersWithCursor(null, 10);
        assertThat(firstPageResponse.getUsers()).hasSize(10);
        assertThat(firstPageResponse.getNextCursorId()).isNotNull();


        Long nextCursorId = firstPageResponse.getNextCursorId();
        AdminUserListResponse secondPageResponse = adminUserService.getUsersWithCursor(nextCursorId, 10);
        assertThat(secondPageResponse.getUsers()).hasSize(10);
        assertThat(secondPageResponse.getNextCursorId()).isNotNull();


        Long thirdCursorId = secondPageResponse.getNextCursorId();
        AdminUserListResponse thirdPageResponse = adminUserService.getUsersWithCursor(thirdCursorId, 10);
        assertThat(thirdPageResponse.getUsers()).hasSize(2);
        assertThat(thirdPageResponse.getNextCursorId()).isNull();
    }

}