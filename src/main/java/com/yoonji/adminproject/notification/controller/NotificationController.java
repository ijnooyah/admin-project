package com.yoonji.adminproject.notification.controller;


import com.yoonji.adminproject.common.dto.response.CommonResponse;
import com.yoonji.adminproject.docs.notification.NotificationControllerDocs;
import com.yoonji.adminproject.notification.dto.response.NotificationListResponse;
import com.yoonji.adminproject.notification.service.NotificationService;
import com.yoonji.adminproject.security.principal.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController implements NotificationControllerDocs {

    private final NotificationService notificationService;

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@AuthenticationPrincipal UserPrincipal principal) {
        return notificationService.createEmitter(principal);
    }

    @GetMapping("/unread")
    public CommonResponse<NotificationListResponse> getUnreadNotifications(
            @AuthenticationPrincipal UserPrincipal principal) {
        return new CommonResponse<>(
                HttpStatus.OK,
                notificationService.getUnreadNotifications(principal)
        );
    }

    @PatchMapping("/{notificationId}/read")
    public CommonResponse<Void> markAsRead(
            @PathVariable Long notificationId,
            @AuthenticationPrincipal UserPrincipal principal) {
        notificationService.markAsRead(notificationId, principal);
        return new CommonResponse<>(HttpStatus.OK);
    }

    @PatchMapping("/read-all")
    public CommonResponse<Void> markAllAsRead(@AuthenticationPrincipal UserPrincipal principal) {
        notificationService.markAllAsRead(principal);
        return new CommonResponse<>(HttpStatus.OK);
    }

}
