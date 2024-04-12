package org.kaanalkim.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ObjectNotFoundById extends RuntimeException {
    public ObjectNotFoundById(String message) {
        super(message);
    }
}