package com.togedy.togedy_server_v2.domain.user.exception.auth;

import com.togedy.togedy_server_v2.global.error.CustomException;
import com.togedy.togedy_server_v2.global.error.ErrorCode;

public class KakaoAccountNotFoundException extends CustomException {
    public KakaoAccountNotFoundException() { super(ErrorCode.KAKAO_ACCOUNT_NOT_FOUND); }
}
