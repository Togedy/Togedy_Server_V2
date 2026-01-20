package com.togedy.togedy_server_v2.domain.user.exception.auth;

import com.togedy.togedy_server_v2.global.error.CustomException;
import com.togedy.togedy_server_v2.global.error.ErrorCode;

public class KakaoTokenExpiredException extends CustomException {
    public KakaoTokenExpiredException() { super(ErrorCode.KAKAO_TOKEN_EXPIRED); }
}
