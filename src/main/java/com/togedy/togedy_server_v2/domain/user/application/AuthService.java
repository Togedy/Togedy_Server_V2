package com.togedy.togedy_server_v2.domain.user.application;

import com.togedy.togedy_server_v2.domain.user.dao.RefreshTokenRepository;
import com.togedy.togedy_server_v2.domain.user.dao.UserRepository;
import com.togedy.togedy_server_v2.domain.user.entity.User;
import com.togedy.togedy_server_v2.domain.user.exception.UserNotFoundException;
import com.togedy.togedy_server_v2.global.security.jwt.JwtTokenInfo;
import com.togedy.togedy_server_v2.global.security.jwt.JwtTokenProvider;
import com.togedy.togedy_server_v2.global.security.jwt.exception.JwtInvalidException;
import com.togedy.togedy_server_v2.global.security.jwt.exception.JwtNotFoundException;
import com.togedy.togedy_server_v2.global.security.jwt.exception.JwtRefreshMismatchException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    public JwtTokenInfo signInUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);

        JwtTokenInfo tokenInfo = jwtTokenProvider.generateTokenInfo(user.getId());

        refreshTokenRepository.save(user.getId(), tokenInfo.getRefreshToken());

        return tokenInfo;
    }

    public JwtTokenInfo reissueToken(String refreshToken) {
        if(!jwtTokenProvider.validateToken(refreshToken)) {
            throw new JwtInvalidException();
        }

        Long userId = Long.parseLong(jwtTokenProvider.getAuthentication(refreshToken).getName());

        String storedToken = refreshTokenRepository.findByUserId(userId)
                .orElseThrow(JwtNotFoundException::new);

        if(!storedToken.equals(refreshToken)) {
            refreshTokenRepository.deleteByUserId(userId);
            throw new JwtRefreshMismatchException();
        }

        JwtTokenInfo newTokenInfo = jwtTokenProvider.generateTokenInfo(userId);
        refreshTokenRepository.deleteByUserId(userId);
        refreshTokenRepository.save(userId, newTokenInfo.getRefreshToken());
        return newTokenInfo;
    }

    public void deleteRefreshToken(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }
}
