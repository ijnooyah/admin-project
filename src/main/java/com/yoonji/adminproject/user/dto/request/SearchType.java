package com.yoonji.adminproject.user.dto.request;

public enum SearchType {
    ALL, EMAIL, NICKNAME;

    public static SearchType fromString(String value) {
        try {
            return SearchType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            return ALL;
        }
    }
}
