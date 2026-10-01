package com.togedy.togedy_server_v2.global.security.jwt.exception;

import com.togedy.togedy_server_v2.global.enums.ErrorCode;

public class JwtUnsupportedException extends JwtException {
    public JwtUnsupportedException() {
        super(ErrorCode.JWT_UNSUPPORTED);
    }
}
