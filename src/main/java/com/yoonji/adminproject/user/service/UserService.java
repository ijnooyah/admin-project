package com.yoonji.adminproject.user.service;



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

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UserResponse getUser(UserPrincipal principal) {
        User findUser = userRepository.findById(principal.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return UserResponse.builder()
                .nickname(findUser.getNickname())
                .email(findUser.getEmail())
                .picture(findUser.getPicture())
                .build();
    }

    @Cacheable(cacheNames = "userCache", key = "'users:' + #id", cacheManager = "cacheManager")
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        log.debug("Cache Miss for user ID: {}", id);
        User findUser = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return UserResponse.builder()
                .nickname(findUser.getNickname())
                .email(findUser.getEmail())
                .picture(findUser.getPicture())
                .build();
    }

    @CacheEvict(cacheNames = "userCache", key = "'users:' + #principal.getId()")
    @Transactional
    public UserResponse updateUser(UserPrincipal principal, UserUpdateRequest request) {
        log.debug("Cache Evict for user ID: {}", principal.getId());
        User findUser = userRepository.findById(principal.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        User updateUser = findUser.update(request);

        return UserResponse.builder()
                .nickname(updateUser.getNickname())
                .email(updateUser.getEmail())
                .picture(updateUser.getPicture())
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
