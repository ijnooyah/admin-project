package com.yoonji.adminproject.admin.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class AdminUserResponse {
    private Long id;
    private String email;
    private String nickname;
    private Set<String> roles;
    private String provider;
    private LocalDateTime createdAt;
    private String profileImageUrl;

    @QueryProjection
    @Builder
    public AdminUserResponse(Long id, String email, String nickname, Set<String> roles, String provider, LocalDateTime createdAt, String profileImageUrl) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.roles = roles;
        this.provider = provider;
        this.createdAt = createdAt;
        this.profileImageUrl = profileImageUrl;
    }
}
