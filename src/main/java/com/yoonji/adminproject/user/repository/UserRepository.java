package com.yoonji.adminproject.user.repository;



import com.yoonji.adminproject.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {
    @EntityGraph(attributePaths = {"userRoles", "userRoles.role"})
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.deleted = false AND u.deletedAt IS NULL")
    Page<User> findAllActiveUsers(Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.deleted = false AND u.deletedAt IS NULL ORDER BY u.id ASC")
    Slice<User> findFirstActiveUsers(Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.deleted = false AND u.deletedAt IS NULL " +
            "AND u.id > :cursorId ORDER BY u.id ASC")
    Slice<User> findActiveUsersAfterCursor(@Param("cursorId") Long cursorId, Pageable pageable);
}
