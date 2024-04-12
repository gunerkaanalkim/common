package org.kaanalkim.common.repository.base;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchFilter {
    private String by;
    private String operator;
    private String value;
}
