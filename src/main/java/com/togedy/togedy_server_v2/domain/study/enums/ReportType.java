package com.togedy.togedy_server_v2.domain.study.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportType {

    ABUSE("욕설/비방, 인신 공격"),
    HATE("혐오/차별 발언"),
    BULLYING("따돌림/괴롭힘"),
    SEXUAL("성적 콘텐츠"),
    VIOLENCE("자해/자살/폭력 위협"),
    PRIVACY("개인정보 노출"),
    ILLEGAL("불법/부적절 거래, 유도"),
    FRAUD("사기/허위 정보"),
    SPAM("스팸/도배/홍보"),
    COPYRIGHT("저작권 침해"),
    OTHER("기타");

    private final String description;
}