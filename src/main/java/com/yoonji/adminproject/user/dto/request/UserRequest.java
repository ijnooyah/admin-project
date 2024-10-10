package com.yoonji.adminproject.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class UserRequest {
    @Schema(hidden = true)
    private String email;
    @Schema(hidden = true)
    private String nickname;
    @Schema(hidden = true)
    private String newPassword;
    @Schema(hidden = true)
    private String password;
}
