package com.togedy.togedy_server_v2.domain.schedule.api;

import com.togedy.togedy_server_v2.domain.schedule.dto.PatchUserScheduleRequest;
import com.togedy.togedy_server_v2.domain.schedule.application.UserScheduleService;
import com.togedy.togedy_server_v2.domain.schedule.dto.GetUserScheduleResponse;
import com.togedy.togedy_server_v2.domain.schedule.dto.PostUserScheduleRequest;
import com.togedy.togedy_server_v2.global.response.ApiResponse;
import com.togedy.togedy_server_v2.global.security.AuthUser;
import com.togedy.togedy_server_v2.global.util.ApiUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/calendars/users")
@Tag(name = "User Schedule", description = "개인 일정 API")
public class UserScheduleController {

    private final UserScheduleService userScheduleService;

    @Operation(summary = "개인 일정 생성", description = "개인 일정을 생성한다.")
    @PostMapping("")
    public ApiResponse<Void> createUserSchedule(@RequestBody PostUserScheduleRequest request,
                                                @AuthenticationPrincipal AuthUser user) {
        userScheduleService.generateUserSchedule(request, user.getId());
        return ApiUtil.successOnly();
    }

    @Operation(summary = "개인 일정 조회", description = "해당 개인 일정 정보를 조회한다.")
    @GetMapping("/{userScheduleId}")
    public ApiResponse<GetUserScheduleResponse> readUserSchedule(@PathVariable Long userScheduleId,
                                                                 @AuthenticationPrincipal AuthUser user) {
        GetUserScheduleResponse response = userScheduleService.findUserSchedule(userScheduleId, user.getId());
        return ApiUtil.success(response);
    }

    @Operation(summary = "개인 일정 수정", description = "해당 개인 일정 정보를 수정한다.")
    @PatchMapping("/{userScheduleId}")
    public ApiResponse<Void> updateUserSchedule(@RequestBody PatchUserScheduleRequest request,
                                                @PathVariable Long userScheduleId,
                                                @AuthenticationPrincipal AuthUser user) {
        userScheduleService.modifyUserSchedule(request, userScheduleId, user.getId());
        return ApiUtil.successOnly();
    }

    @Operation(summary = "개인 일정 제거", description = "해당 개인 일정을 제거한다.")
    @DeleteMapping("/{userScheduleId}")
    public ApiResponse<Void> deleteUserSchedule(@PathVariable Long userScheduleId,
                                                @AuthenticationPrincipal AuthUser user) {
        userScheduleService.removeUserSchedule(userScheduleId, user.getId());
        return ApiUtil.successOnly();
    }
}
