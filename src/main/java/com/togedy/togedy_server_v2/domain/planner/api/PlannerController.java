package com.togedy.togedy_server_v2.domain.planner.api;

import com.togedy.togedy_server_v2.domain.planner.application.PlannerService;
import com.togedy.togedy_server_v2.domain.planner.dto.GetDailyPlannerTopResponse;
import com.togedy.togedy_server_v2.global.response.ApiResponse;
import com.togedy.togedy_server_v2.global.security.AuthUser;
import com.togedy.togedy_server_v2.global.util.ApiUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/planners")
@Tag(name = "Planner", description = "플래너 API")
public class PlannerController {

    private final PlannerService plannerService;

    @Operation(summary = "일별 플래너 상단 조회",
            description = "유저의 일별 플래너 상단을 조회한다. (날짜, 디데이, 총 공부 시간, 플래너 이미지)")
    @GetMapping("/daily")
    public ApiResponse<GetDailyPlannerTopResponse> readDailyPlannerTop(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @AuthenticationPrincipal AuthUser user
    ) {
        GetDailyPlannerTopResponse response = plannerService.findDailyPlannerTop(date, user.getId());
        return ApiUtil.success(response);
    }
}
