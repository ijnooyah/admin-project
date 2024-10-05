package com.yoonji.adminproject.user.repository;

import com.yoonji.adminproject.admin.dto.request.AdminUserSearchCondition;
import com.yoonji.adminproject.admin.dto.response.NewUserStatisticsResponse;
import com.yoonji.adminproject.user.entity.User;

import java.time.LocalDate;
import java.util.List;


public interface UserRepositoryCustom {
    List<User> searchUsersWithCursor(AdminUserSearchCondition condition);
    List<NewUserStatisticsResponse.PeriodStatistics> getNewUserStatistics(String timeUnit, LocalDate startDate, LocalDate endDate);
}
