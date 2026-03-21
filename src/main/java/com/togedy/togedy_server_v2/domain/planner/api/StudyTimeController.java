package com.togedy.togedy_server_v2.domain.planner.api;

import com.togedy.togedy_server_v2.domain.planner.application.StudyTimeService;
import com.togedy.togedy_server_v2.domain.planner.dto.GetDailyTimetableResponse;
import com.togedy.togedy_server_v2.global.response.ApiResponse;
import com.togedy.togedy_server_v2.global.security.AuthUser;
import com.togedy.togedy_server_v2.global.util.ApiUtil;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/v2/planners/daily/timetables")
public class StudyTimeController {

    private final StudyTimeService studyTimeService;

    @Operation(summary = "일별 스터디 타임테이블 조회",
            description = "유저의 일별 스터디 타임테이블을 조회한다. (기준: 오전 5시 ~ 다음날 오전 5시)")
    @GetMapping("")
    public ApiResponse<GetDailyTimetableResponse> readDailyTimetables(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @AuthenticationPrincipal AuthUser user
    ) {
        GetDailyTimetableResponse response = studyTimeService.findDailyTimetables(date, user.getId());
        return ApiUtil.success(response);
    }
}
