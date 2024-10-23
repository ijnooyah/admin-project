package com.yoonji.adminproject.notification.entity;

import lombok.Getter;

@Getter
public enum NotificationType {
    COMMENT("새로운 댓글"),
    ;

    private final String description;

    NotificationType(String description) {
        this.description = description;
    }

}
