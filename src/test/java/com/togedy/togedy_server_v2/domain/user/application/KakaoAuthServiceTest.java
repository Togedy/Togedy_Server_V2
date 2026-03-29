package com.togedy.togedy_server_v2.domain.user.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.togedy.togedy_server_v2.domain.user.dao.AuthProviderRepository;
import com.togedy.togedy_server_v2.domain.user.dao.UserRepository;
import com.togedy.togedy_server_v2.domain.user.dto.KakaoLoginResponse;
import com.togedy.togedy_server_v2.domain.user.dto.KakaoUserInfoResponse;
import com.togedy.togedy_server_v2.domain.user.entity.AuthProvider;
import com.togedy.togedy_server_v2.domain.user.entity.User;
import com.togedy.togedy_server_v2.domain.user.enums.ProviderType;
import com.togedy.togedy_server_v2.global.infrastructure.kakao.KakaoApiClient;
import com.togedy.togedy_server_v2.global.security.jwt.JwtTokenInfo;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class KakaoAuthServiceTest {

    @Mock
    private KakaoApiClient kakaoApiClient;

    @Mock
    private AuthProviderRepository authProviderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthService authService;

    @InjectMocks
    private KakaoAuthService kakaoAuthService;

    @Test
    void 기존_이메일_사용자는_카카오_계정을_연결한_뒤_로그인한다() {
        String kakaoAccessToken = "kakao-access-token";
        String providerUserId = "12345";
        String email = "user@test.com";

        KakaoUserInfoResponse kakaoUser = new KakaoUserInfoResponse();
        ReflectionTestUtils.setField(kakaoUser, "id", 12345L);

        KakaoUserInfoResponse.KakaoAccount kakaoAccount = new KakaoUserInfoResponse.KakaoAccount();
        ReflectionTestUtils.setField(kakaoAccount, "email", email);
        ReflectionTestUtils.setField(kakaoUser, "kakaoAccount", kakaoAccount);

        User user = User.create("tester", email);
        ReflectionTestUtils.setField(user, "id", 1L);
        ReflectionTestUtils.setField(user, "profileCompleted", true);

        JwtTokenInfo tokenInfo = JwtTokenInfo.builder()
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .build();

        given(kakaoApiClient.getUserInfo(kakaoAccessToken)).willReturn(kakaoUser);
        given(authProviderRepository.findByProviderAndProviderUserId(ProviderType.KAKAO, providerUserId))
                .willReturn(Optional.empty());
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(authProviderRepository.save(any(AuthProvider.class))).willAnswer(invocation -> invocation.getArgument(0));
        given(authService.issueToken(user.getId())).willReturn(tokenInfo);

        KakaoLoginResponse response = kakaoAuthService.loginWithKakao(kakaoAccessToken);

        assertThat(response.getJwtTokenInfo()).isEqualTo(tokenInfo);
        assertThat(response.isProfileCompleted()).isTrue();
        verify(userRepository, never()).save(any(User.class));
        verify(authProviderRepository).save(any(AuthProvider.class));
    }

    @Test
    void 신규_카카오_사용자는_임시_유저를_생성한_뒤_로그인한다() {
        String kakaoAccessToken = "kakao-access-token";
        String providerUserId = "67890";
        String email = "new-user@test.com";

        KakaoUserInfoResponse kakaoUser = new KakaoUserInfoResponse();
        ReflectionTestUtils.setField(kakaoUser, "id", 67890L);

        KakaoUserInfoResponse.KakaoAccount kakaoAccount = new KakaoUserInfoResponse.KakaoAccount();
        ReflectionTestUtils.setField(kakaoAccount, "email", email);
        ReflectionTestUtils.setField(kakaoUser, "kakaoAccount", kakaoAccount);

        User savedUser = User.createTemp(email);
        ReflectionTestUtils.setField(savedUser, "id", 2L);

        JwtTokenInfo tokenInfo = JwtTokenInfo.builder()
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .build();

        given(kakaoApiClient.getUserInfo(kakaoAccessToken)).willReturn(kakaoUser);
        given(authProviderRepository.findByProviderAndProviderUserId(ProviderType.KAKAO, providerUserId))
                .willReturn(Optional.empty());
        given(userRepository.findByEmail(email)).willReturn(Optional.empty());
        given(userRepository.save(any(User.class))).willReturn(savedUser);
        given(authProviderRepository.save(any(AuthProvider.class))).willAnswer(invocation -> invocation.getArgument(0));
        given(authService.issueToken(savedUser.getId())).willReturn(tokenInfo);

        KakaoLoginResponse response = kakaoAuthService.loginWithKakao(kakaoAccessToken);

        assertThat(response.getJwtTokenInfo()).isEqualTo(tokenInfo);
        assertThat(response.isProfileCompleted()).isFalse();
        verify(userRepository).save(any(User.class));
        verify(authProviderRepository).save(any(AuthProvider.class));
    }
}
