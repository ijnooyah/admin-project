package com.yoonji.adminproject.admin.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Builder
    public AdminUserResponse(Long id, String email, String nickname, String picture, Set<String> roles, String provider) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.picture = picture;
        this.roles = roles;
        this.provider = provider;
    }
}
