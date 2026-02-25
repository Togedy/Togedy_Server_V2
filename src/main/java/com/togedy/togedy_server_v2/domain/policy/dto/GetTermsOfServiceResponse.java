package com.togedy.togedy_server_v2.domain.policy.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetTermsOfServiceResponse {

    private String termsOfService;

    public static GetTermsOfServiceResponse of(String termsOfService) {
        return GetTermsOfServiceResponse.builder()
                .termsOfService(termsOfService)
                .build();
    }
}
