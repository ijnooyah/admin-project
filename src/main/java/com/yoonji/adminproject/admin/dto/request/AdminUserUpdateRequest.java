package com.yoonji.adminproject.admin.dto.request;

import com.yoonji.adminproject.common.dto.request.user.UserRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserUpdateRequest extends UserRequest {
    private String nickname;
    private String picture;
}
