package com.togedy.togedy_server_v2.global.security.jwt;

import com.togedy.togedy_server_v2.global.error.CustomException;
import com.togedy.togedy_server_v2.global.error.ErrorCode;

public class JwtException extends CustomException {

    public JwtException(ErrorCode errorCode) { super(errorCode); }
}