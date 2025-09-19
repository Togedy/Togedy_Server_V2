package com.togedy.togedy_server_v2.domain.global.factory;

import com.togedy.togedy_server_v2.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TestJwtFactory {

    private final JwtTokenProvider jwtTokenProvider;

    public String createAccessToken(Long userId) {
        return jwtTokenProvider.generateTokenInfo(userId).getAccessToken();
    }

}
