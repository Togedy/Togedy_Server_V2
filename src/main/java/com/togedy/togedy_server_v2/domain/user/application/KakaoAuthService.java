package com.togedy.togedy_server_v2.domain.user.application;

import com.togedy.togedy_server_v2.domain.user.dao.AuthProviderRepository;
import com.togedy.togedy_server_v2.domain.user.dao.UserRepository;
import com.togedy.togedy_server_v2.domain.user.dto.KakaoLoginResponse;
import com.togedy.togedy_server_v2.domain.user.dto.KakaoUserInfoResponse;
import com.togedy.togedy_server_v2.domain.user.entity.AuthProvider;
import com.togedy.togedy_server_v2.domain.user.entity.User;
import com.togedy.togedy_server_v2.domain.user.enums.ProviderType;
import com.togedy.togedy_server_v2.domain.user.exception.auth.KakaoAccountNotFoundException;
import com.togedy.togedy_server_v2.global.infrastructure.kakao.KakaoApiClient;
import com.togedy.togedy_server_v2.global.security.jwt.JwtTokenInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


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
                .findByProviderAndProviderUserId(ProviderType.KAKAO, providerUserId)
                .orElse(null);

        User user;
        boolean completed;

        if (provider != null) {
            user = provider.getUser();
            completed = provider.isProfileCompleted();
        } else {
            user = (email != null)
                    ? userRepository.findByEmail(email)
                    .orElseGet(() -> userRepository.save(User.createTemp(email)))
                    : userRepository.save(User.createTemp(null));

            authProviderRepository.save(
                    AuthProvider.kakao(user, providerUserId, email)
            );
            completed = false;
        }

        JwtTokenInfo jwtTokenInfo = authService.issueToken(user.getId());
        return new KakaoLoginResponse(jwtTokenInfo, completed);
    }
}
