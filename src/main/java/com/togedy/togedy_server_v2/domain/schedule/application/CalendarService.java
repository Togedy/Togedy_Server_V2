package com.togedy.togedy_server_v2.domain.schedule.application;

import com.togedy.togedy_server_v2.domain.schedule.dao.UserScheduleRepository;
import com.togedy.togedy_server_v2.domain.schedule.dto.DailyScheduleListDto;
import com.togedy.togedy_server_v2.domain.schedule.dto.GetDailyCalendarResponse;
import com.togedy.togedy_server_v2.domain.schedule.dto.GetMonthlyCalendarResponse;
import com.togedy.togedy_server_v2.domain.schedule.dto.MonthlyScheduleListDto;
import com.togedy.togedy_server_v2.domain.university.dao.UserUniversityScheduleRepository;
import com.togedy.togedy_server_v2.domain.user.dao.UserRepository;
import com.togedy.togedy_server_v2.domain.user.entity.User;
import com.togedy.togedy_server_v2.global.util.DateTimeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CalendarService {

    private final UserRepository userRepository;
    private final UserScheduleRepository userScheduleRepository;
    private final UserUniversityScheduleRepository userUniversityScheduleRepository;

    @Transactional(readOnly = true)
    public GetMonthlyCalendarResponse findMonthlyCalendar(YearMonth yearMonth, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(RuntimeException::new);

        List<MonthlyScheduleListDto> userSchedule = userScheduleRepository
                .findByUserIdAndYearAndMonth(userId, yearMonth.getYear(), yearMonth.getMonthValue())
                .stream()
                .map(MonthlyScheduleListDto::from)
                .toList();

        List<MonthlyScheduleListDto> universitySchedule = userUniversityScheduleRepository
                .findByUserAndYearAndMonth(userId, yearMonth.getYear(), yearMonth.getMonthValue())
                .stream()
                .flatMap(uus -> uus.getUniversitySchedule()
                        .getAdmissionScheduleList().stream())
                .collect(Collectors.toMap(
                        pivot -> pivot.getUniversitySchedule().getId(),
                        Function.identity(),
                        (first, second) -> first,
                        LinkedHashMap::new
                ))
                .values().stream()
                .map(MonthlyScheduleListDto::from)
                .toList();

        List<MonthlyScheduleListDto> scheduleList = new ArrayList<>();
        scheduleList.addAll(userSchedule);
        scheduleList.addAll(universitySchedule);

        scheduleList.sort(Comparator.<MonthlyScheduleListDto>comparingLong(dto ->
                        DateTimeUtils.durationInSeconds(dto.getStartDate(), dto.getStartTime(),
                        dto.getEndDate(),   dto.getEndTime())).reversed()
                .thenComparing(dto -> DateTimeUtils.toStartDateTime(dto.getStartDate(), dto.getStartTime()))
                .reversed()
        );

        return GetMonthlyCalendarResponse.from(scheduleList);
    }


    @Transactional(readOnly = true)
    public GetDailyCalendarResponse findDailyCalendar(LocalDate date, Long userId) {

        List<DailyScheduleListDto> userSchedule = userScheduleRepository
                .findByUserIdAndDate(userId, date)
                .stream()
                .map(DailyScheduleListDto::from)   // UserSchedule → DTO
                .toList();

        List<DailyScheduleListDto> universitySchedule = userUniversityScheduleRepository
                .findByUserIdAndDate(userId, date)
                .stream()
                .flatMap(uus -> uus.getUniversitySchedule()
                        .getAdmissionScheduleList().stream())
                .collect(Collectors.toMap(
                        pivot -> pivot.getUniversitySchedule().getId(),
                        Function.identity(),
                        (first, second) -> first,
                        LinkedHashMap::new
                ))
                .values().stream()
                .map(DailyScheduleListDto::from)
                .toList();

        List<DailyScheduleListDto> scheduleList = new ArrayList<>();
        scheduleList.addAll(userSchedule);
        scheduleList.addAll(universitySchedule);

        scheduleList.sort(Comparator.<DailyScheduleListDto>comparingLong(dto ->
                                DateTimeUtils.durationInSeconds(dto.getStartDate(), dto.getStartTime(),
                                        dto.getEndDate(),   dto.getEndTime())).reversed()
                        .thenComparing(dto -> DateTimeUtils.toStartDateTime(dto.getStartDate(), dto.getStartTime()))
                .reversed()
        );

        return GetDailyCalendarResponse.from(scheduleList);
    }
}
