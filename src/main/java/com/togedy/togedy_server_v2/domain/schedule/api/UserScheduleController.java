package com.togedy.togedy_server_v2.domain.schedule.api;

import com.togedy.togedy_server_v2.domain.schedule.dto.PatchUserScheduleRequest;
import com.togedy.togedy_server_v2.domain.schedule.application.UserScheduleService;
import com.togedy.togedy_server_v2.domain.schedule.dto.GetUserScheduleResponse;
import com.togedy.togedy_server_v2.domain.schedule.dto.PostUserScheduleRequest;
import com.togedy.togedy_server_v2.global.response.ApiResponse;
import com.togedy.togedy_server_v2.global.security.AuthUser;
import com.togedy.togedy_server_v2.global.util.ApiUtil;
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
public class UserScheduleController {

    private final UserScheduleService userScheduleService;

    @PostMapping("")
    public ApiResponse<Void> createUserSchedule(@RequestBody PostUserScheduleRequest request,
                                                @AuthenticationPrincipal AuthUser user) {
        userScheduleService.generateUserSchedule(request, user.getId());
        return ApiUtil.successOnly();
    }

    @GetMapping("/{userScheduleId}")
    public ApiResponse<GetUserScheduleResponse> readUserSchedule(@PathVariable Long userScheduleId,
                                                                 @AuthenticationPrincipal AuthUser user) {
        GetUserScheduleResponse response = userScheduleService.findUserSchedule(userScheduleId, user.getId());
        return ApiUtil.success(response);
    }

    @PatchMapping("/{userScheduleId}")
    public ApiResponse<Void> updateUserSchedule(@RequestBody PatchUserScheduleRequest request,
                                                @PathVariable Long userScheduleId,
                                                @AuthenticationPrincipal AuthUser user) {
        userScheduleService.modifyUserSchedule(request, userScheduleId, user.getId());
        return ApiUtil.successOnly();
    }

    @DeleteMapping("/{userScheduleId}")
    public ApiResponse<Void> deleteUserSchedule(@PathVariable Long userScheduleId,
                                                @AuthenticationPrincipal AuthUser user) {
        userScheduleService.removeUserSchedule(userScheduleId, user.getId());
        return ApiUtil.successOnly();
    }
}
