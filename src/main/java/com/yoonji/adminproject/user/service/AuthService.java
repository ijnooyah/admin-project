package com.yoonji.adminproject.user.service;


import com.yoonji.adminproject.common.exception.CustomException;
import com.yoonji.adminproject.common.exception.ErrorCode;
import com.yoonji.adminproject.security.principal.UserPrincipal;
import com.yoonji.adminproject.user.dto.request.AdditionalInfoRequest;
import com.yoonji.adminproject.user.dto.request.SignUpRequest;
import com.yoonji.adminproject.user.dto.request.UserUpdateRequest;
import com.yoonji.adminproject.user.dto.response.UserResponse;
import com.yoonji.adminproject.user.entity.Role;
import com.yoonji.adminproject.user.entity.User;
import com.yoonji.adminproject.user.entity.UserRole;
import com.yoonji.adminproject.user.repository.RoleRepository;
import com.yoonji.adminproject.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final Role role;

    @Autowired
    public AuthService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.role = getRoleUser();
    }

    private Role getRoleUser() {
        return roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new CustomException(ErrorCode.ROLE_NOT_FOUND));
    }

    @Transactional
    public UserResponse signup(SignUpRequest request) {
        checkDuplicateEmail(request.getEmail());

        // 사용자권한 생성
        UserRole userRole = UserRole.createUserRole(this.role);

        // 사용자 생성
        User user = User.createLocalUser(request, passwordEncoder, Collections.singleton(userRole));

        // 사용자 저장
        User savedUser = userRepository.save(user);

        return UserResponse.builder()
                .nickname(savedUser.getNickname())
                .email(savedUser.getEmail())
                .picture(savedUser.getPicture())
                .build();
    }

    private void checkDuplicateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
    }

    @Transactional
    public UserResponse updateUser(UserPrincipal principal, AdditionalInfoRequest request) {
        User findUser = userRepository.findById(principal.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        User updateUser = findUser.update(request);

        return UserResponse.builder()
                .nickname(updateUser.getNickname())
                .email(updateUser.getEmail())
                .picture(updateUser.getPicture())
                .build();
    }

}
