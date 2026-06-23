package com.togedy.togedy_server_v2.domain.user.api;

import com.togedy.togedy_server_v2.domain.user.application.AuthService;
import com.togedy.togedy_server_v2.domain.user.application.UserService;
import com.togedy.togedy_server_v2.domain.user.dto.CreateUserRequest;
import com.togedy.togedy_server_v2.domain.user.dto.LoginUserRequest;
import com.togedy.togedy_server_v2.domain.user.dto.LoginUserResponse;
import com.togedy.togedy_server_v2.global.response.ApiResponse;
import com.togedy.togedy_server_v2.global.security.jwt.JwtTokenInfo;
import com.togedy.togedy_server_v2.global.util.ApiUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile({"local", "dev"})
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/dev")
@Tag(name = "DevAuth", description = "개발 전용 인증 API")
public class DevAuthController {

    private final UserService userService;
    private final AuthService authService;

    @Operation(summary = "간편 회원가입", description = """
            
            이메일과 닉네임을 기반으로 회원가입을 한다.
            
            """)
    @PostMapping("/sign-up")
    public ApiResponse<Map<String, Long>> createUser(@Validated @RequestBody CreateUserRequest request) {
        Long userId = userService.generateUser(request);

        Map<String, Long> response = new HashMap<>();
        response.put("userId", userId);

        return ApiUtil.success(response);
    }

    @Operation(summary = "간편 로그인", description = """
            
            이메일 기반 간편 로그인 후 JWT 토큰을 발급한다.
            
            """)
    @PostMapping("/login")
    public ApiResponse<LoginUserResponse> login(@Validated @RequestBody LoginUserRequest request) {
        JwtTokenInfo tokenInfo = authService.signInUser(request.getEmail());
        return ApiUtil.success(new LoginUserResponse(tokenInfo));
    }
}
