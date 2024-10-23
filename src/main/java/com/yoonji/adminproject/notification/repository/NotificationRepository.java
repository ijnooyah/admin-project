package com.yoonji.adminproject.notification.repository;

import com.yoonji.adminproject.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
