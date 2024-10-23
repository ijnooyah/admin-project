package com.yoonji.adminproject.user.service;



import com.yoonji.adminproject.file.entity.File;
import com.yoonji.adminproject.file.service.FileService;
import com.yoonji.adminproject.user.dto.request.UserPasswordRequest;
import com.yoonji.adminproject.user.dto.request.UserUpdateRequest;
import com.yoonji.adminproject.user.dto.response.UserResponse;
import com.yoonji.adminproject.user.entity.User;
import com.yoonji.adminproject.common.exception.CustomException;
import com.yoonji.adminproject.common.exception.ErrorCode;
import com.yoonji.adminproject.user.repository.UserRepository;
import com.yoonji.adminproject.security.principal.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileService fileService;

    @Transactional(readOnly = true)
    public UserResponse getUser(UserPrincipal principal) {
        User findUser = userRepository.findById(principal.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return UserResponse.builder()
                .id(findUser.getId())
                .nickname(findUser.getNickname())
                .email(findUser.getEmail())
                .profileImageUrl(findUser.getProfileImage() != null ?
                        fileService.getFileUrl(findUser.getProfileImage()) :
                        null)
                .build();
    }

    @Cacheable(cacheNames = "userCache", key = "'users:' + #id + ':profile'", cacheManager = "cacheManager")
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        log.debug("Cache Miss for user ID: {}", id);
        User findUser = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return UserResponse.builder()
                .id(findUser.getId())
                .nickname(findUser.getNickname())
                .email(findUser.getEmail())
                .profileImageUrl(findUser.getProfileImage() != null ?
                        fileService.getFileUrl(findUser.getProfileImage()) :
                        null)
                .build();
    }

    @CacheEvict(cacheNames = "userCache", key = "'users:' + #principal.getId() + ':profile'")
    @Transactional
    public UserResponse updateUser(UserPrincipal principal, UserUpdateRequest request, MultipartFile profileImage) throws IOException {
        log.debug("Cache Evict for user ID: {}", principal.getId());
        User findUser = userRepository.findById(principal.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 기존 이미지가 있다면 삭제
        if (findUser.getProfileImage() != null) {
            fileService.deleteFile(findUser.getProfileImage());
        }

        File file = profileImage != null && !profileImage.isEmpty() ?
                fileService.storeFile(profileImage) :
                null;

        findUser.update(request, file);

        return UserResponse.builder()
                .id(findUser.getId())
                .nickname(findUser.getNickname())
                .email(findUser.getEmail())
                .profileImageUrl(findUser.getProfileImage() != null ?
                        fileService.getFileUrl(findUser.getProfileImage()) :
                        null)
                .build();
    }


    @Transactional
    public void changeUserPassword(UserPrincipal principal, UserPasswordRequest request) {
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        user.updatePassword(request.getNewPassword(), passwordEncoder);
        userRepository.save(user);
    }
}
