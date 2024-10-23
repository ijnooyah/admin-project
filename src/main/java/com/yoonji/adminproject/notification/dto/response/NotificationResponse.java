package com.yoonji.adminproject.notification.dto.response;

import com.yoonji.adminproject.notification.entity.EntityType;
import com.yoonji.adminproject.notification.entity.NotificationType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class NotificationResponse {
    private Long id;
    private String message;
    private NotificationType type;
    private EntityType entityType;
    private Long entityId;
    private boolean isRead;
    private LocalDateTime createdAt;

    @Builder
    public NotificationResponse(Long id, String message, NotificationType type, EntityType entityType, Long entityId, boolean isRead, LocalDateTime createdAt) {
        this.id = id;
        this.message = message;
        this.type = type;
        this.entityType = entityType;
        this.entityId = entityId;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }
}
