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
    private String nextCursorId;                   // 다음 페이지의 커서 ID
    private boolean hasNext;

    @Builder
    public AdminUserListResponse(boolean hasNext, String nextCursorId, List<AdminUserResponse> users) {
        this.hasNext = hasNext;
        this.nextCursorId = nextCursorId;
        this.users = users;
    }
}
