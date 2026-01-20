package com.togedy.togedy_server_v2.domain.user.exception.auth;

import com.togedy.togedy_server_v2.global.error.CustomException;
import com.togedy.togedy_server_v2.global.error.ErrorCode;

public class KakaoApiErrorException extends CustomException {
    public KakaoApiErrorException() { super(ErrorCode.KAKAO_API_ERROR); }
}
