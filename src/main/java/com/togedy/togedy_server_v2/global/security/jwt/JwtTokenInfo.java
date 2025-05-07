package com.togedy.togedy_server_v2.global.security.jwt;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JwtTokenInfo {

    private final String accessToken;

    private final String refreshToken;

    public static JwtTokenInfo of(String accessToken, String refreshToken) {
        return JwtTokenInfo.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}