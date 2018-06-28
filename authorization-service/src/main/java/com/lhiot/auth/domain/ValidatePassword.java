package com.lhiot.auth.domain;

import lombok.Getter;

public enum ValidatePassword {
    VALIDATE("验证密码"),

    NOT_VALIDATE("非验证密码");

    @Getter
    private String description;

    ValidatePassword(String description) {
        this.description = description;
    }
}
