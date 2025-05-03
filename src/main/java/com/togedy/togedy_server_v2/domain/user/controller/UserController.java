package com.togedy.togedy_server_v2.domain.user.controller;

import com.togedy.togedy_server_v2.domain.user.dto.CreateUserRequest;
import com.togedy.togedy_server_v2.domain.user.service.UserService;
import com.togedy.togedy_server_v2.global.response.ApiResponse;
import com.togedy.togedy_server_v2.global.util.ApiUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/users")
@Tag(name = "User", description = "유저 관련 API")
public class UserController {

    private final UserService userService;

    @Operation(summary = "간편 회원가입", description = """
            
            이메일/닉네임으로 회원가입을 한다.
            
            """)
    @PostMapping("/sign-up")
    public ApiResponse<Map<String, Long>> createUser(@Validated @RequestBody CreateUserRequest request) {
        Long userId = userService.generateUser(request);

        Map<String, Long> response = new HashMap<>();
        response.put("userId", userId);

        return ApiUtil.success(response);
    }
}
