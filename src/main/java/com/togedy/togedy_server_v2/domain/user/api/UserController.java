package com.togedy.togedy_server_v2.domain.user.api;

import com.togedy.togedy_server_v2.domain.user.application.UserService;
import com.togedy.togedy_server_v2.domain.user.dto.CreateUserRequest;
import com.togedy.togedy_server_v2.domain.user.dto.GetMyPageResponse;
import com.togedy.togedy_server_v2.global.response.ApiResponse;
import com.togedy.togedy_server_v2.global.security.AuthUser;
import com.togedy.togedy_server_v2.global.util.ApiUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/users")
@Tag(name = "User", description = "유저 관리 API")
public class UserController {

    private final UserService userService;

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

    @GetMapping("/me")
    public ApiResponse<GetMyPageResponse> readMyPage(@AuthenticationPrincipal AuthUser user) {
        GetMyPageResponse response = userService.findMyPage(user.getId());
        return ApiUtil.success(response);
    }
}
