package org.kaanalkim.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class GenericSpecificationException extends RuntimeException {
    public GenericSpecificationException(String message) {
        super(message);
    }
}