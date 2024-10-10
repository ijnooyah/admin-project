package com.yoonji.adminproject.user.service;

import com.yoonji.adminproject.common.exception.CustomException;
import com.yoonji.adminproject.common.exception.ErrorCode;
import com.yoonji.adminproject.file.entity.File;
import com.yoonji.adminproject.file.service.FileService;
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
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


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

    @Autowired
    private FileService fileService;

    private User user;

    @BeforeEach
    public void setUp() throws IOException {
        Role role = roleRepository.findByName("ROLE_USER").orElseThrow(() -> new CustomException(ErrorCode.ROLE_NOT_FOUND));

        SignUpRequest request = new SignUpRequest();
        request.setEmail("test@example.com");
        request.setNickname("nickname");
        request.setPassword("password");

        UserRole userRole = UserRole.createUserRole(role);

        User createdUser = User.createLocalUser(request, passwordEncoder, Collections.singleton(userRole));

        // 사용자 저장
        user = userRepository.save(createdUser);

        MockMultipartFile profileImage = new MockMultipartFile(
                "profileImage",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );

        File file = fileService.storeFile(profileImage);
        user.addProfileImage(file);
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
                .extracting(UserResponse::getNickname, UserResponse::getEmail, UserResponse::getProfileImageUrl)
                .containsExactly(user.getNickname(), user.getEmail(), fileService.getFileUrl(user.getProfileImage()));
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
                .extracting(UserResponse::getNickname, UserResponse::getEmail, UserResponse::getProfileImageUrl)
                .containsExactly(user.getNickname(), user.getEmail(), fileService.getFileUrl(user.getProfileImage()));
    }

    @Test
    @DisplayName("updateUser: 사용자 정보를 올바르게 수정해야 한다")
    void updateUser_사용자_정보_수정() throws IOException {
        // given
        UserPrincipal principal = new UserPrincipal(user, null);
        String newNickname = "newNickname";
        MockMultipartFile newProfileImage = new MockMultipartFile(
                "newProfileImage",
                "newtest.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );
        UserUpdateRequest request = new UserUpdateRequest(newNickname);

        // when
        UserResponse response = userService.updateUser(principal, request, newProfileImage);

        // then
        assertThat(response.getNickname()).isEqualTo(newNickname);
        assertThat(response.getProfileImageUrl())
                .startsWith("http://")
                .contains("/files/")
                .endsWith("newtest.jpg");

        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updatedUser.getNickname()).isEqualTo(newNickname);
        assertThat(updatedUser.getProfileImage()).isNotNull();
        assertThat(updatedUser.getProfileImage().getFileName()).isEqualTo("newtest.jpg");
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