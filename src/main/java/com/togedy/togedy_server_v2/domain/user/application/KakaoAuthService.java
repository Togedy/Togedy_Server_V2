package com.togedy.togedy_server_v2.domain.user.application;

import com.togedy.togedy_server_v2.domain.user.dao.AuthProviderRepository;
import com.togedy.togedy_server_v2.domain.user.dao.UserRepository;
import com.togedy.togedy_server_v2.domain.user.dto.KakaoLoginResponse;
import com.togedy.togedy_server_v2.domain.user.dto.KakaoUserInfoResponse;
import com.togedy.togedy_server_v2.domain.user.entity.AuthProvider;
import com.togedy.togedy_server_v2.domain.user.entity.User;
import com.togedy.togedy_server_v2.domain.user.exception.auth.KakaoAccountNotFoundException;
import com.togedy.togedy_server_v2.global.infrastructure.kakao.KakaoApiClient;
import com.togedy.togedy_server_v2.global.security.jwt.JwtTokenInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KakaoAuthService {

    private final KakaoApiClient kakaoApiClient;
    private final AuthProviderRepository authProviderRepository;
    private final UserRepository userRepository;
    private final AuthService authService;

    @Transactional
    public KakaoLoginResponse loginWithKakao(String kakaoAccessToken) {

        KakaoUserInfoResponse kakaoUser = kakaoApiClient.getUserInfo(kakaoAccessToken);
        if (kakaoUser == null || kakaoUser.getId() == null) {
            throw new KakaoAccountNotFoundException();
        }

        String providerUserId = String.valueOf(kakaoUser.getId());
        String email = kakaoUser.getKakaoAccount() != null
                ? kakaoUser.getKakaoAccount().getEmail()
                : null;

        AuthProvider provider = authProviderRepository
                .findByProviderAndProviderUserId("KAKAO", providerUserId)
                .orElse(null);

        Long userId;
        boolean completed;

        if (provider != null) {
            userId = provider.getUserId();
            completed = provider.isProfileCompleted();
        } else {
            User user = User.createTemp(email);
            userRepository.save(user);

            authProviderRepository.save(
                    AuthProvider.kakao(user.getId(), providerUserId, email)
            );
            userId = user.getId();
            completed = false;
        }

        JwtTokenInfo jwtTokenInfo = authService.issueToken(userId);
        return new KakaoLoginResponse(jwtTokenInfo, completed);
    }
}
