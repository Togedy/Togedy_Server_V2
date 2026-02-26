package com.togedy.togedy_server_v2.domain.policy.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetPrivacyPolicyResponse {

    private String privacyPolicy;

    public static GetPrivacyPolicyResponse of(String privacyPolicy) {
        return GetPrivacyPolicyResponse.builder()
                .privacyPolicy(privacyPolicy)
                .build();
    }
}
