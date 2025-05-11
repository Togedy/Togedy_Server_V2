package com.togedy.togedy_server_v2.domain.schedule.api;

import com.togedy.togedy_server_v2.domain.schedule.application.CalendarService;
import com.togedy.togedy_server_v2.domain.schedule.dto.GetDailyCalendarResponse;
import com.togedy.togedy_server_v2.domain.schedule.dto.GetMonthlyCalendarsResponse;
import com.togedy.togedy_server_v2.global.response.ApiResponse;
import com.togedy.togedy_server_v2.global.util.ApiUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/v2/calendars")
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarService calendarService;

    @GetMapping("/monthly")
    public ApiResponse<GetMonthlyCalendarsResponse> readMonthlyCalendar(
            @RequestParam @DateTimeFormat(pattern = "yyyy-mm") YearMonth month,
            Long userId) {
        GetMonthlyCalendarsResponse response = calendarService.findMonthlyCalendar(month, userId);
        return ApiUtil.success(response);
    }

    @GetMapping("/daily")
    public ApiResponse<List<GetDailyCalendarResponse>> readDailyCalendar(
            @RequestParam @DateTimeFormat(pattern = "yyyy-mm-dd") LocalDate date,
            Long userId) {
        List<GetDailyCalendarResponse> response = calendarService.findDailyCalendar(date, userId);
        return ApiUtil.success(response);
    }
}
