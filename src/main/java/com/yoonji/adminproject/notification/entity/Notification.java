package com.yoonji.adminproject.notification.entity;

import com.yoonji.adminproject.common.entity.BaseTimeEntity;
import com.yoonji.adminproject.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Notification extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    private String message;

    private boolean isRead = false;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    private EntityType entityType;

    private Long entityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User target;

    // == 생성 메서드 ==
    public static Notification createNotification(User target, String message, NotificationType type, EntityType entityType, Long entityId) {
        Notification notification = new Notification();
        notification.target = target;
        notification.message = message;
        notification.type = type;
        notification.entityType = entityType;
        notification.entityId = entityId;
        return notification;
    }

    // == 비즈니스 로직 ==
    public void markAsRead() {
        this.isRead = true;
    }

}