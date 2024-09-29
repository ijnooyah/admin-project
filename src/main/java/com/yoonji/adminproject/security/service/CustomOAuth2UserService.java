package com.yoonji.adminproject.security.service;


import com.yoonji.adminproject.common.exception.CustomException;
import com.yoonji.adminproject.common.exception.ErrorCode;
import com.yoonji.adminproject.user.entity.*;
import com.yoonji.adminproject.user.repository.RoleRepository;
import com.yoonji.adminproject.user.repository.UserRepository;
import com.yoonji.adminproject.security.dto.OAuthAttributes;
import com.yoonji.adminproject.security.principal.UserPrincipal;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private Role userRole;

    @PostConstruct
    @Transactional(readOnly = true)
    public void init() {
        this.userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new CustomException(ErrorCode.ROLE_NOT_FOUND));
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        OAuthAttributes oAuthAttributes = OAuthAttributes.of(userRequest, oAuth2User.getAttributes());

        User user = save(oAuthAttributes);

        return new UserPrincipal(user, oAuth2User);
    }

    private User save(OAuthAttributes attributes) {
       User user = userRepository.findByEmail(attributes.getEmail())
                    .orElseGet(() -> createNewOAuthUser(attributes));

       return userRepository.save(user);
    }

    private User createNewOAuthUser(OAuthAttributes attributes) {
        // 사용자권한 생성
        UserRole userRole = UserRole.createUserRole(this.userRole);

        // 사용자 생성
        return User.createOAuthUser(attributes, Collections.singleton(userRole));
    }

}
