package com.togedy.togedy_server_v2.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // GLOBAL
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "G500", "서버 내부에 문제가 발생했습니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "G400", "올바르지 않은 값 또는 형식입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
