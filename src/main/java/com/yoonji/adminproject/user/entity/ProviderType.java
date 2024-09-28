package com.yoonji.adminproject.user.entity;

public enum ProviderType {
    LOCAL, GOOGLE, NAVER;

    public static ProviderType getProviderType(String provider) {
        if (provider == null) {
            throw new IllegalArgumentException("Provider cannot be null");
        }

        return switch (provider.toLowerCase()) {
            case "google" -> ProviderType.GOOGLE;
            case "naver" -> ProviderType.NAVER;
            case "local" -> ProviderType.LOCAL;
            default -> throw new IllegalArgumentException("Unknown provider: " + provider);
        };
    }
}
