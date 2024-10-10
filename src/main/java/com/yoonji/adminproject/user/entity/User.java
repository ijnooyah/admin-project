package com.yoonji.adminproject.user.entity;

import com.yoonji.adminproject.common.entity.BaseTimeEntity;
import com.yoonji.adminproject.file.entity.File;
import com.yoonji.adminproject.security.dto.OAuthAttributes;
import com.yoonji.adminproject.user.dto.request.UserRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static org.springframework.util.StringUtils.hasText;

@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String nickname;
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private ProviderType provider;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserRole> userRoles = new HashSet<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "file_id")
    private File profileImage;

    private boolean deleted = false;

    private LocalDateTime deletedAt;

    // == 연관관계 메서드 ==
    public void addUserRole(UserRole userRole) {
        this.userRoles.add(userRole);
        userRole.updateUser(this);
    }

    public void addProfileImage(File file) {
        this.profileImage = file;
    }

    // == 생성 메서드 ==
    public static <T extends UserRequest> User createLocalUser(T  request, PasswordEncoder passwordEncoder, Set<UserRole> userRoles) {
        User user = new User();
        user.email = request.getEmail();
        user.nickname = request.getNickname();
        user.password = passwordEncoder.encode(request.getPassword());
        user.provider = ProviderType.LOCAL;
        for (UserRole userRole : userRoles) {
            user.addUserRole(userRole);
        }
        return user;
    }

    public static User createOAuthUser(OAuthAttributes attributes, Set<UserRole> userRoles) {
        User user = new User();
        user.email = attributes.getEmail();
        user.provider = ProviderType.getProviderType(attributes.getRegistrationId());
        for (UserRole userRole : userRoles) {
            user.addUserRole(userRole);
        }
        return user;
    }

    // == 수정 메서드 ==
    public <T extends UserRequest> User update(T request, File profileImage) {
        if (hasText(request.getNickname())) {
            this.nickname = request.getNickname();
        }
        if (profileImage != null) {
            this.profileImage = profileImage;
        }
        return this;
    }

    public void updatePassword(String rawPassword, PasswordEncoder passwordEncoder) {
        if (rawPassword != null && !rawPassword.isEmpty()) {
            this.password = passwordEncoder.encode(rawPassword);
        }
    }

    // == 삭제 메서드 ==
    public void delete() {
        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}