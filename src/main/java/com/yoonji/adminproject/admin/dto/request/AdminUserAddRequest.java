package com.yoonji.adminproject.admin.dto.request;

import com.yoonji.adminproject.common.dto.request.user.UserRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserAddRequest extends UserRequest {
    private String email;
    private String nickname;
    private String picture;
    private String password;
    private Set<String> roles;
}
