package com.togedy.togedy_server_v2.global.security.jwt.exception;

import com.togedy.togedy_server_v2.global.enums.ErrorCode;

public class JwtInvalidFormatException extends JwtException {
    public JwtInvalidFormatException() {
        super(ErrorCode.JWT_INVALID_FORMAT);
    }
}
