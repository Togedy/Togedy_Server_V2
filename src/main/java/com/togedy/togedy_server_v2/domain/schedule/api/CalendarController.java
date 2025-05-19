package com.togedy.togedy_server_v2.domain.schedule.api;

import com.togedy.togedy_server_v2.domain.schedule.application.CalendarService;
import com.togedy.togedy_server_v2.domain.schedule.dto.GetDailyCalendarResponse;
import com.togedy.togedy_server_v2.domain.schedule.dto.GetMonthlyCalendarResponse;
import com.togedy.togedy_server_v2.global.response.ApiResponse;
import com.togedy.togedy_server_v2.global.security.AuthUser;
import com.togedy.togedy_server_v2.global.util.ApiUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.YearMonth;

@RestController
@RequestMapping("/api/v2/calendars")
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarService calendarService;

    @GetMapping("/monthly")
    public ApiResponse<GetMonthlyCalendarResponse> readMonthlyCalendar(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth month,
            @AuthenticationPrincipal AuthUser user) {
        GetMonthlyCalendarResponse response = calendarService.findMonthlyCalendar(month, user.getId());
        return ApiUtil.success(response);
    }

    @GetMapping("/daily")
    public ApiResponse<GetDailyCalendarResponse> readDailyCalendar(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @AuthenticationPrincipal AuthUser user) {
        GetDailyCalendarResponse response = calendarService.findDailyCalendar(date, user.getId());
        return ApiUtil.success(response);
    }

    @GetMapping("/d-day")
    public ApiResponse<GetDdayScheduleResponse> readDdaySchedule(@AuthenticationPrincipal AuthUser user) {
        GetDdayScheduleResponse response = calendarService.findDdaySchedule(user.getId());
        return ApiUtil.success(response);
    }
}
