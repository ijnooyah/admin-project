package com.yoonji.adminproject.admin.dto.request;

import com.yoonji.adminproject.user.dto.request.UserRequest;
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
    private String password;
    private Set<String> roles;
}
