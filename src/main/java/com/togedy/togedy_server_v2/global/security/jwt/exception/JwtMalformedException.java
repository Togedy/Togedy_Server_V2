package com.togedy.togedy_server_v2.global.security.jwt.exception;

import com.togedy.togedy_server_v2.global.enums.ErrorCode;

public class JwtMalformedException extends JwtException {
    public JwtMalformedException() {
        super(ErrorCode.JWT_MALFORMED);
    }
}
