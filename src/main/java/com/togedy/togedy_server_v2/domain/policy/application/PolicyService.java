package com.togedy.togedy_server_v2.domain.policy.application;

import com.togedy.togedy_server_v2.domain.policy.dto.GetPrivacyPolicyResponse;
import com.togedy.togedy_server_v2.domain.policy.dto.GetTermsOfServiceResponse;
import com.togedy.togedy_server_v2.domain.policy.enums.Policy;
import org.springframework.stereotype.Service;

@Service
public class PolicyService {

    public GetTermsOfServiceResponse findTermsOfService() {
        return GetTermsOfServiceResponse.of(Policy.TERMS_OF_SERVICE.getContent());
    }

    public GetPrivacyPolicyResponse findPrivacyPolicy() {
        return GetPrivacyPolicyResponse.of(Policy.PRIVACY_POLICY.getContent());
    }
}
