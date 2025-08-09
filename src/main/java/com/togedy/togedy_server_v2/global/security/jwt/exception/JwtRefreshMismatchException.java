package com.togedy.togedy_server_v2.global.security.jwt.exception;

import com.togedy.togedy_server_v2.global.error.ErrorCode;

public class JwtRefreshMismatchException extends JwtException {
    public JwtRefreshMismatchException() { super(ErrorCode.JWT_REFRESH_MISMATCH); }
}
