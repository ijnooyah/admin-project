package com.yoonji.adminproject.user.repository;

import com.yoonji.adminproject.admin.dto.request.AdminUserSearchCondition;
import com.yoonji.adminproject.user.entity.User;

import java.util.List;


public interface UserRepositoryCustom {
    List<User> searchUsersWithCursor(AdminUserSearchCondition condition);
}
