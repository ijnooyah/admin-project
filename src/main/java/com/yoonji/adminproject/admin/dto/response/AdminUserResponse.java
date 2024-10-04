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
    private String picture;
    private Set<String> roles;
    private String provider;
    private LocalDateTime createdAt;

    @QueryProjection
    @Builder
    public AdminUserResponse(Long id, String email, String nickname, String picture, Set<String> roles, String provider, LocalDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.picture = picture;
        this.roles = roles;
        this.provider = provider;
        this.createdAt = createdAt;
    }
}
