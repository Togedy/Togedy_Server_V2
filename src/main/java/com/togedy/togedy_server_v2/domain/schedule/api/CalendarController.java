//package com.togedy.togedy_server_v2.domain.schedule.api;
//
//import com.togedy.togedy_server_v2.domain.schedule.application.CalendarService;
//import com.togedy.togedy_server_v2.domain.schedule.dto.GetAnnouncementResponse;
//import com.togedy.togedy_server_v2.domain.schedule.dto.GetDailyCalendarResponse;
//import com.togedy.togedy_server_v2.domain.schedule.dto.GetDdayScheduleResponse;
//import com.togedy.togedy_server_v2.domain.schedule.dto.GetMonthlyCalendarResponse;
//import com.togedy.togedy_server_v2.global.response.ApiResponse;
//import com.togedy.togedy_server_v2.global.security.AuthUser;
//import com.togedy.togedy_server_v2.global.util.ApiUtil;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import lombok.RequiredArgsConstructor;
//import org.springframework.format.annotation.DateTimeFormat;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.time.LocalDate;
//import java.time.YearMonth;
//
//@RestController
//@RequestMapping("/api/v2/calendars")
//@RequiredArgsConstructor
//@Tag(name = "Calendar", description = "캘린더 API")
//public class CalendarController {
//
//    private final CalendarService calendarService;
//
//    @Operation(summary = "월별 캘린더 조회",
//            description = "유저가 보유 중인 일정을 월별로 조회한다.")
//    @GetMapping("/monthly")
//    public ApiResponse<GetMonthlyCalendarResponse> readMonthlyCalendar(
//            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth month,
//            @AuthenticationPrincipal AuthUser user) {
//        GetMonthlyCalendarResponse response = calendarService.findMonthlyCalendar(month, user.getId());
//        return ApiUtil.success(response);
//    }
//
//    @Operation(summary = "일별 캘린더 조회",
//            description = "유저가 보유 중인 일정을 일별로 조회한다.")
//    @GetMapping("/daily")
//    public ApiResponse<GetDailyCalendarResponse> readDailyCalendar(
//            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
//            @AuthenticationPrincipal AuthUser user) {
//        GetDailyCalendarResponse response = calendarService.findDailyCalendar(date, user.getId());
//        return ApiUtil.success(response);
//    }
//
//    @Operation(summary = "D-Day 조회",
//            description = "유저가 설정한 D-Day 일정 정보를 조회한다.")
//    @GetMapping("/d-day")
//    public ApiResponse<GetDdayScheduleResponse> readDdaySchedule(@AuthenticationPrincipal AuthUser user) {
//        GetDdayScheduleResponse response = calendarService.findDdaySchedule(user.getId());
//        return ApiUtil.success(response);
//    }
//
//    @Operation(summary = "공지사항 조회", description = "공지사항을 조회한다.")
//    @GetMapping("/announcement")
//    public ApiResponse<GetAnnouncementResponse> readAnnouncement() {
//        GetAnnouncementResponse response = calendarService.findAnnouncement();
//        return ApiUtil.success(response);
//    }
//}
