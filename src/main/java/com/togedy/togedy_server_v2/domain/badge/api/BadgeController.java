package com.togedy.togedy_server_v2.domain.badge.api;

import com.togedy.togedy_server_v2.domain.badge.application.BadgeService;
import com.togedy.togedy_server_v2.domain.badge.dto.GetBadgeResponse;
import com.togedy.togedy_server_v2.domain.badge.dto.GetMyBadgesResponse;
import com.togedy.togedy_server_v2.domain.badge.dto.PatchRepresentativeBadgeRequest;
import com.togedy.togedy_server_v2.global.response.ApiResponse;
import com.togedy.togedy_server_v2.global.security.AuthUser;
import com.togedy.togedy_server_v2.global.util.ApiUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/users/me/badges")
@Tag(name = "Badge", description = "뱃지 API")
public class BadgeController {

    private final BadgeService badgeService;

    @Operation(summary = "보유 뱃지 조회", description = "본인의 대표 뱃지와 연도별 보유 뱃지를 조회한다.")
    @GetMapping
    public ApiResponse<GetMyBadgesResponse> readMyBadges(@AuthenticationPrincipal AuthUser user) {
        GetMyBadgesResponse response = badgeService.findMyBadges(user.getId());
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

    @Operation(summary = "대표 뱃지 설정", description = "본인이 보유한 뱃지를 대표 뱃지로 설정한다. 기존 대표 뱃지가 있으면 교체된다.")
    @PatchMapping("/representative")
    public ApiResponse<Void> updateRepresentativeBadge(
            @Validated @RequestBody PatchRepresentativeBadgeRequest request,
            @AuthenticationPrincipal AuthUser user
    ) {
        badgeService.modifyRepresentativeBadge(request, user.getId());
        return ApiUtil.successOnly();
    }

    @Operation(summary = "대표 뱃지 해제", description = "본인의 대표 뱃지를 해제한다.")
    @DeleteMapping("/representative")
    public ApiResponse<Void> deleteRepresentativeBadge(@AuthenticationPrincipal AuthUser user) {
        badgeService.removeRepresentativeBadge(user.getId());
        return ApiUtil.successOnly();
    }
}
