package org.kaanalkim.common.mapper.base;

import org.kaanalkim.common.model.base.AbstractDTO;
import org.kaanalkim.common.model.base.AbstractEntity;

public interface BaseMapper<T extends AbstractEntity, D extends AbstractDTO> {
    D toDTO(T e);

    T toEntity(D d);
}
