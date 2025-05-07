package com.togedy.togedy_server_v2.domain.user.dto;

import com.togedy.togedy_server_v2.global.security.jwt.JwtTokenInfo;
import lombok.Getter;

@Getter
public class LoginUserResponse {

    private final String accessToken;

    private final String refreshToken;

    public LoginUserResponse(JwtTokenInfo jwtTokenInfo) {
        this.accessToken = jwtTokenInfo.getAccessToken();
        this.refreshToken = jwtTokenInfo.getRefreshToken();
    }
}