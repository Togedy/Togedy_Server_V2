package com.togedy.togedy_server_v2.domain.policy.api;

import com.togedy.togedy_server_v2.domain.policy.application.PolicyService;
import com.togedy.togedy_server_v2.domain.policy.dto.GetPrivacyPolicyResponse;
import com.togedy.togedy_server_v2.domain.policy.dto.GetTermsOfServiceResponse;
import com.togedy.togedy_server_v2.global.response.ApiResponse;
import com.togedy.togedy_server_v2.global.util.ApiUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/policies")
public class PolicyController {

    private final PolicyService policyService;

    @GetMapping("/terms-of-service")
    public ApiResponse<GetTermsOfServiceResponse> readTermsOfService() {
        GetTermsOfServiceResponse response = policyService.findTermsOfService();
        return ApiUtil.success(response);
    }

    @GetMapping("/privacy")
    public ApiResponse<GetPrivacyPolicyResponse> readPrivacyPolicy() {
        GetPrivacyPolicyResponse response = policyService.findPrivacyPolicy();
        return ApiUtil.success(response);
    }
}
