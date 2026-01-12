package com.togedy.togedy_server_v2.domain.user.api;

import com.togedy.togedy_server_v2.domain.user.application.KakaoAuthService;
import com.togedy.togedy_server_v2.domain.user.dto.KakaoLoginResponse;
import com.togedy.togedy_server_v2.global.response.ApiResponse;
import com.togedy.togedy_server_v2.global.util.ApiUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/auth/kakao")
@Tag(name = "KakaoAuth", description = "카카오 로그인 API")
public class KakaoAuthController {

    private final KakaoAuthService kakaoAuthService;

    @PostMapping
    public ApiResponse<KakaoLoginResponse> kakaoLogin(
            @RequestHeader("Kakao-Access-Token") String kakaoToken
    ) {
        return ApiUtil.success(kakaoAuthService.loginWithKakao(kakaoToken));
    }
}
