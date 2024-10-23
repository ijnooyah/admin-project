package com.yoonji.adminproject.notification.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class NotificationListResponse {
    private List<NotificationResponse> notifications;
    private Long unreadCount;  // 읽지 않은 알림 총 개수
    private Long totalCount;   // 전체 알림 개수

    @Builder
    public NotificationListResponse(List<NotificationResponse> notifications, Long unreadCount, Long totalCount) {
        this.notifications = notifications;
        this.unreadCount = unreadCount;
        this.totalCount = totalCount;
    }

    public static NotificationListResponse of(List<NotificationResponse> notifications, Long unreadCount, Long totalCount) {
        return NotificationListResponse.builder()
                .notifications(notifications)
                .unreadCount(unreadCount)
                .totalCount(totalCount)
                .build();
    }
}
