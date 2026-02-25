package com.togedy.togedy_server_v2.domain.user.api;

import com.togedy.togedy_server_v2.domain.user.application.UserService;
import com.togedy.togedy_server_v2.domain.user.dto.CreateUserRequest;
import com.togedy.togedy_server_v2.domain.user.dto.GetMyPageResponse;
import com.togedy.togedy_server_v2.domain.user.dto.GetMySettingsResponse;
import com.togedy.togedy_server_v2.domain.user.dto.PatchMarketingConsentedSettingRequest;
import com.togedy.togedy_server_v2.domain.user.dto.PatchNicknameRequest;
import com.togedy.togedy_server_v2.domain.user.dto.PatchProfileImageRequest;
import com.togedy.togedy_server_v2.domain.user.dto.PatchPushNotificationSettingRequest;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
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

    @Operation(summary = "마이페이지 조회", description = "본인의 마이페이지를 조회한다.")
    @GetMapping("/me")
    public ApiResponse<GetMyPageResponse> readMyPage(@AuthenticationPrincipal AuthUser user) {
        GetMyPageResponse response = userService.findMyPage(user.getId());
        return ApiUtil.success(response);
    }

    @Operation(summary = "유저 설정 조회", description = "본인의 알림 설정 및 이메일을 조회한다.")
    @GetMapping("/me/settings")
    public ApiResponse<GetMySettingsResponse> readMySettings(@AuthenticationPrincipal AuthUser user) {
        GetMySettingsResponse response = userService.findMySettings(user.getId());
        return ApiUtil.success(response);
    }

    @Operation(summary = "유저 푸시알림 여부 변경", description = "본인의 푸시알림 여부를 변경한다.")
    @PatchMapping("/me/settings/push")
    public ApiResponse<Void> updatePushNotificationSetting(
            @RequestBody PatchPushNotificationSettingRequest request,
            @AuthenticationPrincipal AuthUser user
    ) {
        userService.modifyPushNotificationSetting(request, user.getId());
        return ApiUtil.successOnly();
    }

    @Operation(summary = "유저 마케팅 수신 동의 여부 변경", description = "본인의 마케팅 수신 동의 여부를 변경한다.")
    @PatchMapping("/me/settings/marketing")
    public ApiResponse<Void> updateMarketingConsentedSetting(
            @RequestBody PatchMarketingConsentedSettingRequest request,
            @AuthenticationPrincipal AuthUser user
    ) {
        userService.modifyMarketingConsentedSetting(request, user.getId());
        return ApiUtil.successOnly();
    }

    @Operation(summary = "유저 닉네임 변경", description = "본인의 닉네임을 변경한다.")
    @PatchMapping("/me/nickname")
    public ApiResponse<Void> updateNickname(
            @RequestBody PatchNicknameRequest request,
            @AuthenticationPrincipal AuthUser user
    ) {
        userService.modifyNickname(request, user.getId());
        return ApiUtil.successOnly();
    }

    @Operation(summary = "유저 프로필 이미지 변경", description = "본인의 프로필 이미지를 변경한다.")
    @PatchMapping("/me/profile-image")
    public ApiResponse<Void> updateProfileImage(
            @ModelAttribute PatchProfileImageRequest request,
            @AuthenticationPrincipal AuthUser user
    ) {
        userService.modifyProfileImage(request, user.getId());
        return ApiUtil.successOnly();
    }
}
