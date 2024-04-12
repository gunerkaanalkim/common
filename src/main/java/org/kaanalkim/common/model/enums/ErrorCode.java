package org.kaanalkim.common.model.enums;

import lombok.Getter;

@Getter
public enum ErrorCode {
    NOT_FOUND("9000"),
    ROLE_ALREADY_ASSIGNED("9001"),
    ROLE_USER_NOT_FOUND("9002"),
    PERMISSION_ALREADY_ASSIGNED("9003"),
    PERMISSION_NOT_FOUND("9004");

    private final String code;

    ErrorCode(String code) {
        this.code = code;
    }
}
