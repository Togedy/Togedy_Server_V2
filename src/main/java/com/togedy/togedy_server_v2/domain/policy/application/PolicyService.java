package com.togedy.togedy_server_v2.domain.policy.application;

import com.togedy.togedy_server_v2.domain.policy.dto.GetPrivacyPolicyResponse;
import com.togedy.togedy_server_v2.domain.policy.dto.GetTermsOfServiceResponse;
import com.togedy.togedy_server_v2.domain.policy.enums.Policy;
import org.springframework.stereotype.Service;

@Service
public class PolicyService {

    /**
     * 서비스 이용약관을 조회한다.
     * <p>
     * 시스템에 정의된 이용약관 정책 내용을 반환한다. 약관 내용은 {@code Policy.TERMS_OF_SERVICE}에 정의된 값을 기반으로 제공된다.
     * </p>
     *
     * @return 서비스 이용약관 조회 응답 DTO
     */
    public GetTermsOfServiceResponse findTermsOfService() {
        return GetTermsOfServiceResponse.of(Policy.TERMS_OF_SERVICE.getContent());
    }

    /**
     * 개인정보 처리방침을 조회한다.
     * <p>
     * 시스템에 정의된 개인정보 처리방침 내용을 반환한다. 정책 내용은 {@code Policy.PRIVACY_POLICY}에 정의된 값을 기반으로 제공된다.
     * </p>
     *
     * @return 개인정보 처리방침 조회 응답 DTO
     */
    public GetPrivacyPolicyResponse findPrivacyPolicy() {
        return GetPrivacyPolicyResponse.of(Policy.PRIVACY_POLICY.getContent());
    }
}
