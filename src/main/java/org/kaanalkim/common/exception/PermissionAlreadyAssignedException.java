package org.kaanalkim.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FOUND)
public class PermissionAlreadyAssignedException extends RuntimeException {
    public PermissionAlreadyAssignedException(String message) {
        super(message);
    }
}