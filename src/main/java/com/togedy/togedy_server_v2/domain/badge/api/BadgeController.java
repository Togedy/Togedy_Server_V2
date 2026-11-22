package com.togedy.togedy_server_v2.domain.badge.api;

import com.togedy.togedy_server_v2.domain.badge.application.BadgeService;
import com.togedy.togedy_server_v2.domain.badge.dto.GetBadgeResponse;
import com.togedy.togedy_server_v2.domain.badge.dto.GetMyBadgesResponse;
import com.togedy.togedy_server_v2.global.response.ApiResponse;
import com.togedy.togedy_server_v2.global.security.AuthUser;
import com.togedy.togedy_server_v2.global.util.ApiUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/users/me/badges")
@Tag(name = "Badge", description = "뱃지 API")
public class BadgeController {

    private final BadgeService badgeService;

    @Operation(summary = "보유 뱃지 조회", description = "본인의 연도별 보유 뱃지를 조회한다.")
    @GetMapping
    public ApiResponse<GetMyBadgesResponse> readMyBadges(@AuthenticationPrincipal AuthUser user) {
        GetMyBadgesResponse response = badgeService.findMyBadges(user.getId());
        return ApiUtil.success(response);
    }

    @Operation(summary = "새 뱃지 조회", description = "본인이 아직 확인하지 않은 뱃지를 상세 정보 목록으로 조회하고 확인 처리한다.")
    @GetMapping("/new")
    public ApiResponse<List<GetBadgeResponse>> readNewBadges(@AuthenticationPrincipal AuthUser user) {
        List<GetBadgeResponse> response = badgeService.findNewBadges(user.getId());
        return ApiUtil.success(response);
    }

    @Operation(summary = "뱃지 상세 조회", description = "본인이 보유한 뱃지의 이름, 이미지, 설명을 조회한다.")
    @GetMapping("/{badgeId}")
    public ApiResponse<GetBadgeResponse> readMyBadge(
            @PathVariable Long badgeId,
            @AuthenticationPrincipal AuthUser user
    ) {
        GetBadgeResponse response = badgeService.findMyBadge(badgeId, user.getId());
        return ApiUtil.success(response);
    }
}
