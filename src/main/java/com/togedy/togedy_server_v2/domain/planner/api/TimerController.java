package com.togedy.togedy_server_v2.domain.planner.api;

import com.togedy.togedy_server_v2.domain.planner.application.TimerService;
import com.togedy.togedy_server_v2.domain.planner.dto.PostTimerStartRequest;
import com.togedy.togedy_server_v2.domain.planner.dto.PostTimerStartResponse;
import com.togedy.togedy_server_v2.global.response.ApiResponse;
import com.togedy.togedy_server_v2.global.security.AuthUser;
import com.togedy.togedy_server_v2.global.util.ApiUtil;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/timers")
public class TimerController {

    private final TimerService timerService;

    @Operation(summary = "타이머 시작", description = "과목 기준으로 스터디 타이머를 시작한다.")
    @PostMapping("/start")
    public ApiResponse<PostTimerStartResponse> startTimer(
            @RequestBody PostTimerStartRequest request,
            @AuthenticationPrincipal AuthUser user
    ) {
        PostTimerStartResponse response = timerService.startTimer(request, user.getId());
        return ApiUtil.success(response);
    }
}
