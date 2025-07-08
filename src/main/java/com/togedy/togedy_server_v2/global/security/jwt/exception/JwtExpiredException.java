package com.togedy.togedy_server_v2.global.security.jwt.exception;

import com.togedy.togedy_server_v2.global.error.ErrorCode;

public class JwtExpiredException extends JwtException {
    public JwtExpiredException() {
        super(ErrorCode.JWT_EXPIRED);
    }
}
