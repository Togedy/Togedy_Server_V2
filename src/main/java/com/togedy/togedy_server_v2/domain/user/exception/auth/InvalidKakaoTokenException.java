package com.togedy.togedy_server_v2.domain.user.exception.auth;

import com.togedy.togedy_server_v2.global.enums.ErrorCode;
import com.togedy.togedy_server_v2.global.exception.CustomException;

public class InvalidKakaoTokenException extends CustomException {
    public InvalidKakaoTokenException() {
        super(ErrorCode.INVALID_KAKAO_TOKEN);
    }
}
