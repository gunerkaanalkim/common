package org.kaanalkim.common.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JWTVerificationResponse {
    private Boolean isValid;
    private Map<Object, Object> claims;
}
