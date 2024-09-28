package com.yoonji.adminproject.admin.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AdminUserListResponse {

    private List<AdminUserResponse> users;
    private int totalPages;
    private long totalElements;
    private int currentPage;

    @Builder
    public AdminUserListResponse(int currentPage, long totalElements, int totalPages, List<AdminUserResponse> users) {
        this.currentPage = currentPage;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.users = users;
    }
}
