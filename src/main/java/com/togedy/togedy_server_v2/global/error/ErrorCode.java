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


    // USER
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U404", "사용자를 찾을 수 없습니다."),
    DUPLICATED_EMAIL(HttpStatus.CONFLICT, "U409", "이미 존재하는 이메일입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
