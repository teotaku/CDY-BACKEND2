package com.cdy.cdy.domain.users.entity;

public enum UserCategory {


    DESIGN("디자인"),

    CODING("코딩"),

    EDITING("영상편집");

    private final String description;

    UserCategory(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
