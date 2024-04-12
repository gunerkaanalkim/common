package org.kaanalkim.common.service.base;

import org.kaanalkim.common.model.enums.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public abstract class AbstractService {
    public String className = getName(getClass());
    protected Logger logger = LoggerFactory.getLogger(getClass());

    protected String getName(@SuppressWarnings("rawtypes") Class clazz) {
        String[] strings = clazz.getName().split("\\.");
        if (strings.length > 0) {
            return strings[strings.length - 1];
        }
        return "Object";
    }

    protected void validateId(Object id) {
        if (id == null || id.equals(0L)) {
            throw new IllegalArgumentException("Id may not be null or zero!");
        }
    }

    protected Pageable getPager(Integer pageNo, Integer pageSize, String column, String order) {
        Sort sort = order.equals("asc") ? Sort.by(column).ascending() : Sort.by(column).descending();
        return PageRequest.of(pageNo, pageSize, sort);
    }

    protected String getErrorMessage(ErrorCode errorCode, String hint) {
        return String.format("System Error Code : %s, Hint: %s", errorCode, hint);
    }
}