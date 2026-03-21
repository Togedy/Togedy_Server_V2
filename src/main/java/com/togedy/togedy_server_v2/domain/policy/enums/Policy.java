package com.togedy.togedy_server_v2.domain.policy.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Policy {

    TERMS_OF_SERVICE("서비스 이용 약관"),
    PRIVACY_POLICY("개인정보 처리방침");

    private final String content;
}
