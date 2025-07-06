package com.togedy.togedy_server_v2.global.security.jwt.exception;

import com.togedy.togedy_server_v2.global.error.ErrorCode;

public class JwtInvalidSignatureException extends JwtException {
    public JwtInvalidSignatureException() {
        super(ErrorCode.JWT_INVALID_SIGNATURE);
    }
}
