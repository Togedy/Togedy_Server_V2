package com.togedy.togedy_server_v2.domain.badge.enums;

public enum DateRuleType {
    SOLAR,   // 양력 월·일로 매년 자동 지정
    LUNAR,   // 음력 월·일로 매년 자동 지정
    MANUAL   // 어드민이 날짜를 직접 지정
}
