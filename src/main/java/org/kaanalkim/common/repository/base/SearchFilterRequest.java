package org.kaanalkim.common.repository.base;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchFilterRequest {
    public Integer pageNo;
    public Integer pageSize;
    public String column;
    public String order;
    public String operator;
    public List<SearchFilter> filters;
}
