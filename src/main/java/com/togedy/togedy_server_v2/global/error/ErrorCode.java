package com.togedy.togedy_server_v2.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // GLOBAL
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "G500", "서버 내부에 문제가 발생했습니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "G400", "올바르지 않은 값 또는 형식입니다."),

    // JWT (1000)
    JWT_EXPIRED(HttpStatus.UNAUTHORIZED, "J1000", "만료된 토큰입니다."),
    JWT_UNSUPPORTED(HttpStatus.BAD_REQUEST, "J1001", "지원하지 않는 토큰입니다."),
    JWT_MALFORMED(HttpStatus.BAD_REQUEST, "J1002", "손상된 토큰입니다."),
    JWT_INVALID_SIGNATURE(HttpStatus.UNAUTHORIZED, "J1003", "토큰 서명이 유효하지 않습니다."),
    JWT_INVALID(HttpStatus.UNAUTHORIZED, "J1004", "유효하지 않은 토큰입니다."),
    JWT_MISSING(HttpStatus.UNAUTHORIZED, "J1005", "토큰이 존재하지 않습니다."),
    JWT_INVALID_FORMAT(HttpStatus.BAD_REQUEST, "J1006", "잘못된 토큰 형식입니다."),

    // USER (2000)
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U2000", "사용자를 찾을 수 없습니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "U2001", "동일한 닉네임이 존재합니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "U2002", "동일한 이메일이 존재합니다."),

    // CATEGORY (3000)
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "C3000", "해당 카테고리를 찾을 수 없습니다."),
    CATEGORY_NOT_OWNED(HttpStatus.UNAUTHORIZED, "C3001", "해당 유저의 카테고리가 아닙니다."),
    DUPLICATE_CATEGORY(HttpStatus.BAD_REQUEST, "C3002", "이름과 색상이 동일한 카테고리가 존재합니다."),

    // USER_SCHEDULE (4000)
    USER_SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND, "US4000", "해당 개인 일정을 찾을 수 없습니다."),
    USER_SCHEDULE_NOT_OWNED(HttpStatus.UNAUTHORIZED, "US4001", "해당 유저의 개인 일정이 아닙니다."),

    // UNIVERSITY_SCHEDULE (5000)
    UNIVERSITY_SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND, "UNS5000", "해당 대학 일정을 찾을 수 없습니다."),
    INVALID_ADMISSION_TYPE(HttpStatus.BAD_REQUEST, "UNS5001", "유효하지 않은 입시 유형입니다."),

    // APP_CONFIG (6000)
    APP_CONFIG_NOT_FOUND(HttpStatus.NOT_FOUND, "A6000", "앱 설정이 존재하지 않습니다."),

    // UNIVERSITY (7000)
    UNIVERSITY_NOT_FOUND(HttpStatus.NOT_FOUND, "UN6001", "해당 대학을 찾을 수 없습니다."),

    // UNIVERSITY_ADMISSION_METHOD (8000)
    UNIVERSITY_ADMISSION_METHOD_NOT_FOUND(HttpStatus.NOT_FOUND, "UAM7001", "해당 대학 입시 전형을 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
