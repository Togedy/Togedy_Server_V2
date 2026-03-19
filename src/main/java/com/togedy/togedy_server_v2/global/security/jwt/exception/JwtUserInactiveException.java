package com.togedy.togedy_server_v2.global.security.jwt.exception;

import com.togedy.togedy_server_v2.global.error.ErrorCode;

public class JwtUserInactiveException extends JwtException {

    public JwtUserInactiveException() {
        super(ErrorCode.USER_INACTIVE);
    }
}
