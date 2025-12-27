package com.togedy.togedy_server_v2.domain.user.dto;

import com.togedy.togedy_server_v2.global.security.jwt.JwtTokenInfo;
import lombok.Getter;

@Getter
public class KakaoLoginResponse {

    private JwtTokenInfo jwtTokenInfo;

    private boolean profileCompleted;

    public KakaoLoginResponse(JwtTokenInfo jwtTokenInfo, Boolean profileCompleted) {
        this.jwtTokenInfo = jwtTokenInfo;
        this.profileCompleted = profileCompleted;
    }
}
