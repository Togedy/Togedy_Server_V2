package com.togedy.togedy_server_v2.global.security.jwt.exception;

import com.togedy.togedy_server_v2.global.enums.ErrorCode;

public class JwtUserInactiveException extends JwtException {

    public JwtUserInactiveException() {
        super(ErrorCode.USER_INACTIVE);
    }
}
