package org.kaanalkim.common.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthorizationVerificationRequest {
    private String username;
    private long realmId;
    private String requestPath;
}
