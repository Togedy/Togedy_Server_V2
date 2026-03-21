package com.togedy.togedy_server_v2.domain.support.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum InquiryType {

    BUG("버그 신고"),
    FEATURE("기능 제안"),
    USAGE("사용 문의"),
    ACCOUNT("계정/로그인"),
    ETC("기타");

    private final String description;
}
