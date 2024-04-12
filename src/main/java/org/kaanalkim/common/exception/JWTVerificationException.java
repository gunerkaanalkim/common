package org.kaanalkim.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class JWTVerificationException extends RuntimeException {
    public JWTVerificationException(String message) {
        super(message);
    }
}