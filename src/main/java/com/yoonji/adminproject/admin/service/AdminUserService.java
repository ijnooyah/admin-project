package com.yoonji.adminproject.admin.service;


import com.yoonji.adminproject.admin.dto.request.AdminUserAddRequest;
import com.yoonji.adminproject.admin.dto.request.AdminUserRolesRequest;
import com.yoonji.adminproject.admin.dto.request.AdminUserSearchCondition;
import com.yoonji.adminproject.admin.dto.request.AdminUserUpdateRequest;
import com.yoonji.adminproject.admin.dto.response.AdminUserListResponse;
import com.yoonji.adminproject.admin.dto.response.AdminUserResponse;
import com.yoonji.adminproject.admin.dto.response.NewUserStatisticsResponse;
import com.yoonji.adminproject.common.exception.CustomException;
import com.yoonji.adminproject.common.exception.ErrorCode;
import com.yoonji.adminproject.file.entity.File;
import com.yoonji.adminproject.file.service.FileService;
import com.yoonji.adminproject.user.dto.request.SortType;
import com.yoonji.adminproject.user.entity.ProviderType;
import com.yoonji.adminproject.user.entity.Role;
import com.yoonji.adminproject.user.entity.User;
import com.yoonji.adminproject.user.entity.UserRole;
import com.yoonji.adminproject.user.repository.RoleRepository;
import com.yoonji.adminproject.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
public class AdminUserService {

    private final FileService fileService;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final Set<Role> roles;

    @Autowired
    public AdminUserService(PasswordEncoder passwordEncoder, UserRepository userRepository, RoleRepository roleRepository, FileService fileService) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.roles = getAllRoles();
        this.fileService = fileService;
    }

    private Set<Role> getAllRoles() {
        return new HashSet<>(roleRepository.findAll());
    }

    private AdminUserResponse convertToAdminUserResponse(User user) {
        Set<String> roles = user.getUserRoles().stream()
                .map(userRole -> userRole.getRole().getName())
                .collect(Collectors.toSet());

        return AdminUserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .roles(roles)
                .provider(user.getProvider().name())
                .nickname(user.getNickname())
                .createdAt(user.getCreatedAt())
                .profileImageUrl(user.getProfileImage() != null ?
                        fileService.getFileUrl(user.getProfileImage()) :
                        null)
                .build();
    }

    @Transactional
    public void deleteUser(Long id) {
        User findUser = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        findUser.delete();
    }

    @CacheEvict(cacheNames = "userCache", key = "'users:' + #id + ':profile'")
    @Transactional
    public AdminUserResponse updateUser(Long id, AdminUserUpdateRequest request, MultipartFile profileImage) throws IOException {
        User findUser = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 기존 이미지가 있다면 삭제
        if (findUser.getProfileImage() != null) {
            fileService.deleteFile(findUser.getProfileImage());
        }

        File file = profileImage != null && !profileImage.isEmpty() ?
                fileService.storeFile(profileImage) :
                null;

        User updateUser = findUser.update(request, file);

        return AdminUserResponse.builder()
                .email(updateUser.getEmail())
                .nickname(updateUser.getNickname())
                .roles(updateUser.getUserRoles().stream()
                        .map(userRole -> userRole.getRole().getName())
                        .collect(Collectors.toSet()))
                .provider(updateUser.getProvider().name())
                .profileImageUrl(updateUser.getProfileImage() != null ?
                        fileService.getFileUrl(updateUser.getProfileImage()) :
                        null)
                .build();
    }

    private User updateRoles(AdminUserRolesRequest request, User user) {
        user.getUserRoles().clear();

        request.getRoles().forEach(roleName -> {
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new CustomException(ErrorCode.ROLE_NOT_FOUND));
            UserRole userRole = UserRole.createUserRole(role);
            user.addUserRole(userRole);
        });

        return user;
    }

    @Transactional
    public AdminUserResponse updateUserRoles(Long id, AdminUserRolesRequest request) {
        User findUser = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        User updateUser = updateRoles(request, findUser);

        return AdminUserResponse.builder()
                .email(updateUser.getEmail())
                .nickname(updateUser.getNickname())
                .roles(updateUser.getUserRoles().stream()
                        .map(userRole -> userRole.getRole().getName())
                        .collect(Collectors.toSet()))
                .provider(updateUser.getProvider().name())
                .build();
    }

    @Transactional
    public AdminUserResponse addUser(AdminUserAddRequest request) {
        checkDuplicateEmail(request.getEmail());

        // 사용자권한 생성
        Set<UserRole> userRoles = this.roles.stream()
                .filter(role ->  request.getRoles().contains(role.getName()))
                .map(UserRole::createUserRole)
                .collect(Collectors.toSet());

        // 사용자 생성
        User user = User.createLocalUser(request, passwordEncoder, userRoles);

        // 사용자 저장
        User savedUser = userRepository.save(user);

        return AdminUserResponse.builder()
                .email(savedUser.getEmail())
                .nickname(savedUser.getNickname())
                .roles(savedUser.getUserRoles().stream()
                        .map(userRole -> userRole.getRole().getName())
                        .collect(Collectors.toSet()))
                .provider(ProviderType.LOCAL.name())
                .build();
    }

    private void checkDuplicateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
    }

    @Transactional(readOnly = true)
    public AdminUserListResponse searchUsersWithCursor(AdminUserSearchCondition condition) {
        List<User> users = userRepository.searchUsersWithCursor(condition);

        boolean hasNext = false;
        String nextCursorId = null;

        if (users.size() > condition.getSize()) {
            hasNext = true;
            Long userId = users.getLast().getId();

            nextCursorId = switch (SortType.valueOf(condition.getSortType())) {
                case CREATED_AT -> generateCreatedAtCursorId(users.getLast().getCreatedAt(), userId);
                case EMAIL -> users.getLast().getEmail();
            };
        }

        List<AdminUserResponse> adminUserResponse = users.stream()
                .limit(condition.getSize())
                .map(this::convertToAdminUserResponse)
                .collect(Collectors.toList());


        return AdminUserListResponse.builder()
                .users(adminUserResponse)
                .hasNext(hasNext)
                .nextCursorId(nextCursorId) // 다음 커서 ID 설정
                .build();
    }

    private String generateCreatedAtCursorId(LocalDateTime createdAt, Long userId){
        // 1. LocalDateTime을 DATE_FORMAT와 동일한 포맷으로 변환 (yyMMddHHmmss)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMddHHmmss");
        String formattedCreatedAt = createdAt.format(formatter);

        // 2. 포맷된 createdAt을 20자리로 왼쪽을 '0'으로 채움
        String customCursorCreatedAt = StringUtils.leftPad(formattedCreatedAt, 20, '0');

        // 3. userId를 문자열로 변환하고 10자리로 왼쪽을 '0'으로 채움
        String customCursorId = StringUtils.leftPad(userId.toString(), 10, '0');

        // 4. 두 값을 연결하여 반환
        return customCursorCreatedAt + customCursorId;
    }

    public NewUserStatisticsResponse getNewUserStatistics(String timeUnit, LocalDate startDate, LocalDate endDate) {
        List<NewUserStatisticsResponse.PeriodStatistics> stats = userRepository.getNewUserStatistics(timeUnit, startDate, endDate);

        calculateGrowthRates(stats);

        int totalNewUsers = stats.stream().mapToInt(NewUserStatisticsResponse.PeriodStatistics::getNewUsers).sum();
        double averageNewUsers = stats.stream().mapToInt(NewUserStatisticsResponse.PeriodStatistics::getNewUsers).average().orElse(0);

        NewUserStatisticsResponse.PeriodStatistics maxStat = stats.stream()
                .max(Comparator.comparingInt(NewUserStatisticsResponse.PeriodStatistics::getNewUsers))
                .orElse(null);
        NewUserStatisticsResponse.PeriodStatistics minStat = stats.stream()
                .min(Comparator.comparingInt(NewUserStatisticsResponse.PeriodStatistics::getNewUsers))
                .orElse(null);

        NewUserStatisticsResponse.PeakNewUsers maxNewUsers = (maxStat != null)
                ? new NewUserStatisticsResponse.PeakNewUsers(maxStat.getPeriod(), maxStat.getNewUsers())
                : null;
        NewUserStatisticsResponse.PeakNewUsers minNewUsers = (minStat != null)
                ? new NewUserStatisticsResponse.PeakNewUsers(minStat.getPeriod(), minStat.getNewUsers())
                : null;

        return NewUserStatisticsResponse.builder()
                .timeUnit(timeUnit)
                .startDate(startDate)
                .endDate(endDate)
                .totalNewUsers(totalNewUsers)
                .statistics(stats)
                .averageNewUsersPerPeriod(averageNewUsers)
                .maxNewUsers(maxNewUsers)
                .minNewUsers(minNewUsers)
                .build();
    }

    private void calculateGrowthRates(List<NewUserStatisticsResponse.PeriodStatistics> stats) {
        if (stats.isEmpty()) {
            return;
        }

        IntStream
                .range(1, stats.size())
                .forEach(i -> {
                    NewUserStatisticsResponse.PeriodStatistics current = stats.get(i);
                    NewUserStatisticsResponse.PeriodStatistics previous = stats.get(i - 1);
                    double growthRate = (current.getNewUsers() - previous.getNewUsers()) / (double) previous.getNewUsers() * 100;
                    current.setGrowthRate(Math.round(growthRate * 100.0) / 100.0);  // 소수점 두 자리까지 반올림
                });

        stats.getFirst().setGrowthRate(null);  // 첫 번째 기간의 성장률은 null
    }
}
