package com.yoonji.adminproject.admin.service;


import com.yoonji.adminproject.admin.dto.request.AdminUserAddRequest;
import com.yoonji.adminproject.admin.dto.request.AdminUserCursorRequest;
import com.yoonji.adminproject.admin.dto.request.AdminUserRolesRequest;
import com.yoonji.adminproject.admin.dto.request.AdminUserUpdateRequest;
import com.yoonji.adminproject.admin.dto.response.AdminUserListResponse;
import com.yoonji.adminproject.admin.dto.response.AdminUserResponse;
import com.yoonji.adminproject.common.exception.CustomException;
import com.yoonji.adminproject.common.exception.ErrorCode;
import com.yoonji.adminproject.user.dto.response.UserResponse;
import com.yoonji.adminproject.user.entity.ProviderType;
import com.yoonji.adminproject.user.entity.Role;
import com.yoonji.adminproject.user.entity.User;
import com.yoonji.adminproject.user.entity.UserRole;
import com.yoonji.adminproject.user.repository.RoleRepository;
import com.yoonji.adminproject.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AdminUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final Set<Role> roles;

    @Autowired
    public AdminUserService(PasswordEncoder passwordEncoder, UserRepository userRepository, RoleRepository roleRepository, Set<Role> roles) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.roles = getAllRoles();
    }

    private Set<Role> getAllRoles() {
        return new HashSet<>(roleRepository.findAll());
    }

    @Transactional(readOnly = true)
    public AdminUserListResponse getUsersWithCursor(Long cursorId, int size) {
        Slice<User> userSlice = (cursorId == null || cursorId == 0) ?
                userRepository.findFirstActiveUsers(PageRequest.of(0, size + 1)) :
                userRepository.findActiveUsersAfterCursor(cursorId, PageRequest.of(0, size + 1));

        List<AdminUserResponse> adminUserResponse = userSlice.getContent().stream()
                .limit(size)
                .map(this::convertToAdminUserResponse)
                .collect(Collectors.toList());

        // 마지막 사용자 ID를 커서로 설정
        Long nextCursorId = userSlice.hasNext() ?
                userSlice.getContent().getLast().getId() : null;

        return AdminUserListResponse.builder()
                .users(adminUserResponse)
                .nextCursorId(nextCursorId) // 다음 커서 ID 설정
                .build();
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
    public AdminUserResponse updateUser(Long id, AdminUserUpdateRequest request) {
        User findUser = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        User updateUser = findUser.update(request);
        return AdminUserResponse.builder()
                .email(updateUser.getEmail())
                .nickname(updateUser.getNickname())
                .picture(updateUser.getPicture())
                .roles(updateUser.getUserRoles().stream()
                        .map(userRole -> userRole.getRole().getName())
                        .collect(Collectors.toSet()))
                .provider(updateUser.getProvider().name())
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
                .picture(updateUser.getPicture())
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
                .picture(savedUser.getPicture())
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


}
