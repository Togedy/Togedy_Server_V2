package com.togedy.togedy_server_v2.global.security.jwt.exception;

import com.togedy.togedy_server_v2.global.error.ErrorCode;

public class JwtMissingException extends JwtException {
    public JwtMissingException() {
        super(ErrorCode.JWT_MISSING);
    }
}
