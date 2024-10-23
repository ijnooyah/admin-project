package com.yoonji.adminproject.security.principal;

import com.yoonji.adminproject.user.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class UserPrincipal implements OAuth2User, UserDetails {

    private final Long id;
    private final String email;
    private final String password;
    private final String nickname;
    private final Set<String> roles;
    private final OAuth2User oAuth2User;
    private final User user;

    public UserPrincipal(User user, OAuth2User oAuth2User) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.nickname = user.getNickname();
        this.roles = user.getUserRoles().stream()
                .map(userRole -> userRole.getRole().getName())
                .collect(Collectors.toSet());
        this.oAuth2User = oAuth2User;
        this.user = user;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oAuth2User != null ? oAuth2User.getAttributes() : Map.of();
    }

    @Override
    public String getName() {
        return oAuth2User != null ? oAuth2User.getName() : "";
    }
}
