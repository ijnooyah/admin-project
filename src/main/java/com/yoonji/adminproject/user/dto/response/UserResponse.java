package com.yoonji.adminproject.user.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String email;
    private String nickname;
    private String profileImageUrl;

    @Builder
    public UserResponse(Long id, String email, String nickname, String profileImageUrl) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
    }
}
