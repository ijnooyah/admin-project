package com.yoonji.adminproject.admin.service;

import com.yoonji.adminproject.admin.dto.request.AdminUserAddRequest;
import com.yoonji.adminproject.admin.dto.request.AdminUserRolesRequest;
import com.yoonji.adminproject.admin.dto.request.AdminUserUpdateRequest;
import com.yoonji.adminproject.admin.dto.response.AdminUserResponse;
import com.yoonji.adminproject.admin.dto.response.NewUserStatisticsResponse;
import com.yoonji.adminproject.common.exception.CustomException;
import com.yoonji.adminproject.common.exception.ErrorCode;
import com.yoonji.adminproject.user.dto.request.SignUpRequest;
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

import java.time.LocalDate;
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
    void testGetDailyNewUserStatistics() {
        // Given
        String timeUnit = "day";
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 1, 31);

        // When
        NewUserStatisticsResponse response = adminUserService.getNewUserStatistics(timeUnit, startDate, endDate);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getTimeUnit()).isEqualTo(timeUnit);
        assertThat(response.getStartDate()).isEqualTo(startDate);
        assertThat(response.getEndDate()).isEqualTo(endDate);
        assertThat(response.getTotalNewUsers()).isPositive();
        assertThat(response.getStatistics()).isNotEmpty();
        assertThat(response.getStatistics()).hasSize(2); // kim.minsoo@example.com, jo.insung@example.com
    }

    @Test
    void testGetWeeklyNewUserStatistics() {
        // Given
        String timeUnit = "week";
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);

        // When
        NewUserStatisticsResponse response = adminUserService.getNewUserStatistics(timeUnit, startDate, endDate);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getTimeUnit()).isEqualTo(timeUnit);
        assertThat(response.getStartDate()).isEqualTo(startDate);
        assertThat(response.getEndDate()).isEqualTo(endDate);
        assertThat(response.getTotalNewUsers()).isPositive();
        assertThat(response.getStatistics()).isNotEmpty();
        assertThat(response.getStatistics()).hasSize(17); /* 1. kim.minsoo@example.com, 2. park.jiyoung@gmail.com, 3. lee.sungmin@naver.com, 4. hong.gildong@gmail.com, 5. kang.sora@naver.com,
        6. jung.minho@example.com, 7. kwon.jiae@naver.com, 8. yoo.jaesuk@example.com, 9. song.joongki@gmail.com, 10. jo.insung@example.com,
        11. bae.suzy@gmail.com, 12. son.yejin@example.com, 13. kim.soohyun@gmail.com, 14. ha.jungwoo@example.com, 15. jang.keunseok@example.com,
        16. im.yoona@gmail.com , 17. park.seojoon@naver.com */
    }

    @Test
    void testGetMonthlyNewUserStatistics() {
        // Given
        String timeUnit = "month";
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);

        // When
        NewUserStatisticsResponse response = adminUserService.getNewUserStatistics(timeUnit, startDate, endDate);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getTimeUnit()).isEqualTo(timeUnit);
        assertThat(response.getStartDate()).isEqualTo(startDate);
        assertThat(response.getEndDate()).isEqualTo(endDate);
        assertThat(response.getTotalNewUsers()).isPositive();
        assertThat(response.getStatistics()).isNotEmpty();
        assertThat(response.getStatistics()).hasSize(11); // 12월에 가입한 한명 삭제됨
    }

    @Test
    void testGetYearlyNewUserStatistics() {
        // Given
        String timeUnit = "year";
        LocalDate startDate = LocalDate.of(2022, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);

        // When
        NewUserStatisticsResponse response = adminUserService.getNewUserStatistics(timeUnit, startDate, endDate);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getTimeUnit()).isEqualTo(timeUnit);
        assertThat(response.getStartDate()).isEqualTo(startDate);
        assertThat(response.getEndDate()).isEqualTo(endDate);
        assertThat(response.getTotalNewUsers()).isPositive();
        assertThat(response.getStatistics()).isNotEmpty();
        assertThat(response.getStatistics()).hasSize(3); // 2022, 2023, 2024
    }

}