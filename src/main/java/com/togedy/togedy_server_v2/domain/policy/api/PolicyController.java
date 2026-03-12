package com.togedy.togedy_server_v2.domain.policy.api;

import com.togedy.togedy_server_v2.domain.policy.application.PolicyService;
import com.togedy.togedy_server_v2.domain.policy.dto.GetPrivacyPolicyResponse;
import com.togedy.togedy_server_v2.domain.policy.dto.GetTermsOfServiceResponse;
import com.togedy.togedy_server_v2.global.response.ApiResponse;
import com.togedy.togedy_server_v2.global.util.ApiUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/policies")
@Tag(name = "Policy", description = "정책 API")
public class PolicyController {

    private final PolicyService policyService;

    @Operation(summary = "서비스 이용 약관 조회", description = "서비스 이용 약관을 조회한다.")
    @GetMapping("/terms-of-service")
    public ApiResponse<GetTermsOfServiceResponse> readTermsOfService() {
        GetTermsOfServiceResponse response = policyService.findTermsOfService();
        return ApiUtil.success(response);
    }

    @Operation(summary = "개인정보 처리방침 조회", description = "개인정보 처리방침을 조회한다.")
    @GetMapping("/privacy")
    public ApiResponse<GetPrivacyPolicyResponse> readPrivacyPolicy() {
        GetPrivacyPolicyResponse response = policyService.findPrivacyPolicy();
        return ApiUtil.success(response);
    }
}
