package com.yoonji.adminproject.notification.service;

import com.yoonji.adminproject.common.exception.CustomException;
import com.yoonji.adminproject.common.exception.ErrorCode;
import com.yoonji.adminproject.notification.dto.response.NotificationListResponse;
import com.yoonji.adminproject.notification.dto.response.NotificationResponse;
import com.yoonji.adminproject.notification.entity.EntityType;
import com.yoonji.adminproject.notification.entity.Notification;
import com.yoonji.adminproject.notification.entity.NotificationType;
import com.yoonji.adminproject.notification.repository.NotificationRepository;
import com.yoonji.adminproject.security.principal.UserPrincipal;
import com.yoonji.adminproject.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final Map<Long, SseEmitter> userEmitters = new ConcurrentHashMap<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final NotificationRepository notificationRepository;

    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60; // 60 minutes


    public SseEmitter createEmitter(UserPrincipal principal) {
        log.debug("Creating emitter for user: {}", principal.getId());
        removeEmitterIfExists(principal.getId());

        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        userEmitters.put(principal.getId(), emitter);

        // 연결 종료 시 처리
        emitter.onCompletion(() -> {
            log.debug("Emitter completed for user: {}", principal.getId());
            removeEmitterIfExists(principal.getId());
        });

        emitter.onTimeout(() -> {
            log.debug("Emitter timed out for user: {}", principal.getId());
            removeEmitterIfExists(principal.getId());
        });

        emitter.onError((e) -> {
            log.error("Emitter error for user: {}", principal.getId(), e);
            removeEmitterIfExists(principal.getId());
        });
        // 연결 즉시 더미 이벤트 전송
        sendDummyEvent(emitter);

        return emitter;
    }

    private void removeEmitterIfExists(Long userId) {
        SseEmitter emitter = userEmitters.remove(userId);
        if (emitter != null) {
            try {
                emitter.complete();
                log.debug("Removed and completed emitter for user: {}", userId);
            } catch (Exception e) {
                log.error("Error while removing emitter for user: {}", userId, e);
            }
        }
    }

    private void sendDummyEvent(SseEmitter emitter) {
        executor.execute(() -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("connect")
                        .data("Connected!"));
                log.debug("Sent dummy event to emitter");
            } catch (Exception e) {
                log.error("Error while sending dummy event", e);
            }
        });
    }

    @Transactional
    public void sendNotification(User user, String message, NotificationType type,
                                 EntityType entityType, Long entityId) {
        try {
            // 알림 생성 및 저장
            Notification notification = Notification.createNotification(
                    user,
                    message,
                    type,
                    entityType,
                    entityId
            );

            Notification savedNotification = notificationRepository.save(notification);
            log.debug("Saved notification: {}", savedNotification.getId());

            // 실시간 알림 전송
            sendRealTimeNotification(savedNotification);

        } catch (Exception e) {
            log.error("Error while sending notification", e);
            throw new RuntimeException("알림 전송 중 오류가 발생했습니다.", e);
        }
    }

    private void sendRealTimeNotification(Notification notification) {
        SseEmitter emitter = userEmitters.get(notification.getTarget().getId());
        if (emitter != null) {
            executor.execute(() -> {
                try {
                    NotificationResponse notificationDto = convertToNotificationResponse(notification);
                    emitter.send(SseEmitter.event()
                            .name("notification")
                            .data(notificationDto));
                    log.debug("Sent real-time notification: {}", notification.getId());
                } catch (Exception e) {
                    log.error("Error while sending real-time notification", e);
                    removeEmitterIfExists(notification.getTarget().getId());
                }
            });
        }
    }

    private NotificationResponse convertToNotificationResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .message(notification.getMessage())
                .type(notification.getType())
                .entityType(notification.getEntityType())
                .entityId(notification.getEntityId())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }

    @Transactional
    public void markAsRead(Long notificationId, UserPrincipal principal) {
        Notification notification = notificationRepository.findByIdAndTarget(notificationId, principal.getUser())
                .orElseThrow(() -> new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND));
        notification.markAsRead();
    }

    @Transactional
    public void markAllAsRead(UserPrincipal principal) {
        List<Notification> notifications = notificationRepository.findUnreadByTargetId(principal.getId());
        notifications.forEach(Notification::markAsRead);
    }

    @Transactional(readOnly = true)
    public NotificationListResponse getUnreadNotifications(UserPrincipal principal) {
        List<NotificationResponse> notifications = notificationRepository.findUnreadByTargetId(principal.getId())
                .stream()
                .map(this::convertToNotificationResponse)
                .toList();

        Long unreadCount = notificationRepository.countUnreadByTargetId(principal.getId());
        Long totalCount = notificationRepository.countByTargetId(principal.getId());

        return NotificationListResponse.of(notifications, unreadCount, totalCount);
    }
}
