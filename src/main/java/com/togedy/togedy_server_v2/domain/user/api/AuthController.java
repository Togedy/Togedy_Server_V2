package com.togedy.togedy_server_v2.domain.user.api;

import com.togedy.togedy_server_v2.domain.user.application.AuthService;
import com.togedy.togedy_server_v2.domain.user.dto.LoginUserRequest;
import com.togedy.togedy_server_v2.domain.user.dto.LoginUserResponse;
import com.togedy.togedy_server_v2.domain.user.dto.TokenRequest;
import com.togedy.togedy_server_v2.global.response.ApiResponse;
import com.togedy.togedy_server_v2.global.security.AuthUser;
import com.togedy.togedy_server_v2.global.security.jwt.JwtTokenInfo;
import com.togedy.togedy_server_v2.global.util.ApiUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/auth")
@Tag(name = "Auth", description = "인증 및 토큰 관리 API")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "간편 로그인", description = """
            
            이메일 기반 간편 로그인 후 JWT 토큰을 발급한다.
            
            """)
    @PostMapping("/login")
    public ApiResponse<LoginUserResponse> login(@Validated @RequestBody LoginUserRequest request) {
        JwtTokenInfo tokenInfo = authService.signInUser(request.getEmail());
        return ApiUtil.success(new LoginUserResponse(tokenInfo));
    }

    @Operation(summary = "토큰 재발급",description = """
            
            리프레시 토큰을 이용해 새로운 액세스 토큰을 발급한다.
            
            """)
    @PostMapping("/reissue")
    public ApiResponse<JwtTokenInfo> reissue(@RequestBody TokenRequest request) {
        return ApiUtil.success(authService.reissueToken(request.getRefreshToken()));
    }

    @Operation(summary = "로그아웃", description = """
            
            리프레시 토큰을 삭제하여 로그아웃 처리한다.
            
            """)
    @PostMapping("/logout")
    public ApiResponse<Void> logout(@AuthenticationPrincipal AuthUser authUser) {
        authService.deleteRefreshToken(authUser.getId());
        return ApiUtil.successOnly();
    }
}
