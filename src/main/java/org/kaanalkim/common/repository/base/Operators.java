package org.kaanalkim.common.repository.base;

import lombok.Getter;

@Getter
public enum Operators {
    AND("and"),
    OR("or"),
    EQUAL("equal"),
    NOT_EQUAL("notEqual"),
    GREATER_THAN_OR_EQUAL("greaterThanOrEqual"),
    GREATER_THAN("greaterThan"),
    LESS_THAN_OR_EQUAL("lessThanOrEqual"),
    LESS_THAN("lessThan"),
    LIKE("like");

    private final String operator;

    Operators(String operator) {
        this.operator = operator;
    }
}
