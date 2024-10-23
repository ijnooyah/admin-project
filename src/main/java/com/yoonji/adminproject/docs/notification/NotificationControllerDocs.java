package com.yoonji.adminproject.docs.notification;

import com.yoonji.adminproject.common.dto.response.CommonResponse;
import com.yoonji.adminproject.notification.dto.response.NotificationListResponse;
import com.yoonji.adminproject.security.principal.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Tag(name = "Notification", description = "Notification API")
public interface NotificationControllerDocs {
    @Operation(summary = "실시간 알림 구독")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "실시간 알림 구독 성공"),
    })
    SseEmitter subscribe(@AuthenticationPrincipal UserPrincipal principal);

    @Operation(summary = "읽지 않은 알림 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "읽지 않은 알림 목록 조회 성공"),
    })
    CommonResponse<NotificationListResponse> getUnreadNotifications(
            @AuthenticationPrincipal UserPrincipal principal);

    @Operation(summary = "특정 알림 읽음 처리")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "특정 알림 읽음 처리 성공"),
    })
    CommonResponse<Void> markAsRead(
            @PathVariable Long notificationId,
            @AuthenticationPrincipal UserPrincipal principal);

    @Operation(summary = "모든 알림 읽음 처리")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "모든 알림 읽음 처리 성공"),
    })
    CommonResponse<Void> markAllAsRead(@AuthenticationPrincipal UserPrincipal principal);

}
