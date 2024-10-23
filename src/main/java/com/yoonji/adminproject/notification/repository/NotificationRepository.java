package com.yoonji.adminproject.notification.repository;

import com.yoonji.adminproject.notification.entity.Notification;
import com.yoonji.adminproject.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Optional<Notification> findByIdAndTarget(Long id, User target);

    @Query("SELECT n FROM Notification n WHERE n.target.id = :targetId AND n.isRead = false ORDER BY n.createdAt DESC")
    List<Notification> findUnreadByTargetId(Long targetId);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.target.id = :targetId AND n.isRead = false")
    Long countUnreadByTargetId(Long targetId);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.target.id = :targetId")
    Long countByTargetId(Long targetId);
}
