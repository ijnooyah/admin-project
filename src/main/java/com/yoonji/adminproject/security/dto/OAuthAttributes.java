package com.yoonji.adminproject.security.dto;

import com.yoonji.adminproject.user.entity.ProviderType;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;

import java.util.Map;

@Getter
public class OAuthAttributes {
    private final Map<String, Object> attributes;
    private final String registrationId;
    private final String nameAttributeKey;
    private final String nickname;
    private final String email;
    private final String picture;

    @Builder
    public OAuthAttributes(Map<String, Object> attributes, String email, String nickname, String nameAttributeKey, String picture, String registrationId) {
        this.attributes = attributes;
        this.email = email;
        this.nickname = nickname;
        this.nameAttributeKey = nameAttributeKey;
        this.picture = picture;
        this.registrationId = registrationId;
    }

    public static OAuthAttributes of(OAuth2UserRequest userRequest, Map<String, Object> attributes) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        if (ProviderType.NAVER.name().toLowerCase().equals(registrationId)) {
            return ofNaver(registrationId, "id", attributes);
        }
        return ofGoogle(registrationId, userNameAttributeName, attributes);
    }

    private static OAuthAttributes ofNaver(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return OAuthAttributes.builder()
                .email((String) response.get("email"))
                .registrationId(registrationId)
                .attributes(response)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    private static OAuthAttributes ofGoogle(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .email((String) attributes.get("email"))
                .attributes(attributes)
                .registrationId(registrationId)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }
}
