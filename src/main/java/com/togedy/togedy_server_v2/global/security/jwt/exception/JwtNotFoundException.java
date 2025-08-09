package com.togedy.togedy_server_v2.global.security.jwt.exception;

import com.togedy.togedy_server_v2.global.error.ErrorCode;

public class JwtNotFoundException extends JwtException{
    public JwtNotFoundException() {
        super(ErrorCode.JWT_NOT_FOUND);
    }
}
