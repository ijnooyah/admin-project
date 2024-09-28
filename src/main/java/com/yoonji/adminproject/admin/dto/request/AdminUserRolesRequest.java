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
public class AdminUserRolesRequest extends UserRequest {
    private Set<String> roles;
}
