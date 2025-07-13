package com.togedy.togedy_server_v2.global.security.jwt.exception;

import com.togedy.togedy_server_v2.global.error.ErrorCode;

public class JwtInvalidException extends JwtException {
    public JwtInvalidException() {
        super(ErrorCode.JWT_INVALID);
    }
}
