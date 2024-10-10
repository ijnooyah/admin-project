package com.yoonji.adminproject.user.service;


import com.yoonji.adminproject.common.exception.CustomException;
import com.yoonji.adminproject.common.exception.ErrorCode;
import com.yoonji.adminproject.file.entity.File;
import com.yoonji.adminproject.file.service.FileService;
import com.yoonji.adminproject.security.principal.UserPrincipal;
import com.yoonji.adminproject.user.dto.request.AdditionalInfoRequest;
import com.yoonji.adminproject.user.dto.request.SignUpRequest;
import com.yoonji.adminproject.user.dto.response.UserResponse;
import com.yoonji.adminproject.user.entity.Role;
import com.yoonji.adminproject.user.entity.User;
import com.yoonji.adminproject.user.entity.UserRole;
import com.yoonji.adminproject.user.repository.RoleRepository;
import com.yoonji.adminproject.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileService fileService;
    private final Role role;

    public AuthService(UserRepository userRepository, RoleRepository roleRepository, FileService fileService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.fileService = fileService;
        this.passwordEncoder = passwordEncoder;
        this.role = getRoleUser();
    }

    private Role getRoleUser() {
        return roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new CustomException(ErrorCode.ROLE_NOT_FOUND));
    }

    @Transactional
    public UserResponse signup(SignUpRequest request, MultipartFile profileImage) throws IOException {
        checkDuplicateEmail(request.getEmail());

        // 사용자권한 생성
        UserRole userRole = UserRole.createUserRole(this.role);

        // 사용자 생성
        User user = User.createLocalUser(request, passwordEncoder, Collections.singleton(userRole));

        if (profileImage != null && !profileImage.isEmpty()) {
            File file = fileService.storeFile(profileImage);
            user.addProfileImage(file);
        }

        // 사용자 저장
        User savedUser = userRepository.save(user);

        return UserResponse.builder()
                .nickname(savedUser.getNickname())
                .email(savedUser.getEmail())
                .profileImageUrl(savedUser.getProfileImage() != null ?
                        fileService.getFileUrl(savedUser.getProfileImage()) :
                        null)
                .build();
    }

    private void checkDuplicateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
    }

    @Transactional
    public UserResponse updateUser(UserPrincipal principal, AdditionalInfoRequest request, MultipartFile profileImage) throws IOException {
        User findUser = userRepository.findById(principal.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        File file = profileImage != null && !profileImage.isEmpty() ?
                fileService.storeFile(profileImage) : null;

        User updateUser = findUser.update(request, file);

        return UserResponse.builder()
                .nickname(updateUser.getNickname())
                .email(updateUser.getEmail())
                .profileImageUrl(updateUser.getProfileImage() != null ?
                        fileService.getFileUrl(updateUser.getProfileImage()) :
                        null)
                .build();
    }

}
