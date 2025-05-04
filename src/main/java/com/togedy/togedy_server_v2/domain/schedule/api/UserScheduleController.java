package com.togedy.togedy_server_v2.domain.schedule.api;

import com.togedy.togedy_server_v2.domain.schedule.application.UserScheduleService;
import com.togedy.togedy_server_v2.domain.schedule.dto.GetUserScheduleResponse;
import com.togedy.togedy_server_v2.domain.schedule.dto.PostUserScheduleRequest;
import com.togedy.togedy_server_v2.global.response.ApiResponse;
import com.togedy.togedy_server_v2.global.util.ApiUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
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
    public ApiResponse<Void> createUserSchedule(@RequestBody PostUserScheduleRequest request, Long userId) {
        userScheduleService.generateUserSchedule(request, userId);
        return ApiUtil.successOnly();
    }

    @GetMapping("/{userScheduleId}")
    public ApiResponse<GetUserScheduleResponse> readUserSchedule(@PathVariable Long userScheduleId, Long userId) {
        GetUserScheduleResponse response = userScheduleService.findUserSchedule(userScheduleId, userId);
        return ApiUtil.success(response);
    }
}
