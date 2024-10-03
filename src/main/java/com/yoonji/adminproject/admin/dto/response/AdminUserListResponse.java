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

    private List<AdminUserResponse> users;      // 사용자 목록
    private int totalPages;                      // 총 페이지 수
    private long totalElements;                  // 총 사용자 수
    private int currentPage;                     // 현재 페이지
    private Long nextCursorId;                   // 다음 페이지의 커서 ID

    @Builder
    public AdminUserListResponse(int currentPage, long totalElements, int totalPages,
                                 List<AdminUserResponse> users, Long nextCursorId) {
        this.currentPage = currentPage;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.users = users;
        this.nextCursorId = nextCursorId;      // 다음 커서 ID 추가
    }
}
