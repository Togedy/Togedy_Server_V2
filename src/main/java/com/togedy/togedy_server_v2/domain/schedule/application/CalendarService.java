package com.togedy.togedy_server_v2.domain.schedule.application;

import com.togedy.togedy_server_v2.domain.schedule.dao.UserScheduleRepository;
import com.togedy.togedy_server_v2.domain.schedule.dto.GetDailyCalendarResponse;
import com.togedy.togedy_server_v2.domain.schedule.dto.GetMonthlyCalendarsResponse;
import com.togedy.togedy_server_v2.domain.schedule.dto.ScheduleListDto;
import com.togedy.togedy_server_v2.domain.university.dao.UserUniversityScheduleRepository;
import com.togedy.togedy_server_v2.domain.university.entity.UserUniversitySchedule;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CalendarService {

    private final UserRepository userRepository;
    private final UserScheduleRepository userScheduleRepository;
    private final UserUniversityScheduleRepository userUniversityScheduleRepository;

    @Transactional(readOnly = true)
    public GetMonthlyCalendarsResponse findMonthlyCalendar(YearMonth yearMonth, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(RuntimeException::new);

        List<ScheduleListDto> scheduleList = new ArrayList<>(userScheduleRepository
                .findByUserIdAndYearAndMonth(userId, yearMonth.getYear(), yearMonth.getMonthValue())
                .stream().map(ScheduleListDto::from).toList());

        Set<String> seen = new HashSet<>();
        scheduleList.addAll(userUniversityScheduleRepository.findByUserAndYearAndMonth(userId, yearMonth.getYear(), yearMonth.getMonthValue())
                        .stream().map(UserUniversitySchedule::getUniversitySchedule)
                        .map(ScheduleListDto::from)
                        .filter(dto -> {
                            String key = dto.getStartDate() + "|" +
                                    dto.getEndDate()   + "|" +
                                    dto.getScheduleName() + "|" +
                                    dto.getUniversityAdmissionStage();
                    return seen.add(key);
                })
                .toList());

        scheduleList.sort(
                Comparator.comparingLong((ScheduleListDto schedule) ->
                                DateTimeUtils.durationInSeconds(schedule.getStartDate(), schedule.getEndDate()))
                        .thenComparing(ScheduleListDto::getStartDate)
                        .reversed()
        );

        return GetMonthlyCalendarsResponse.from(scheduleList);
    }


    @Transactional
    public List<GetDailyCalendarResponse> findDailyCalendar(LocalDate date, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(RuntimeException::new);

        List<GetDailyCalendarResponse> scheduleList = new ArrayList<>(userScheduleRepository
                .findByUserIdAndDate(userId, date)
                .stream().map(GetDailyCalendarResponse::from).toList());

        Set<String> seen = new HashSet<>();
        scheduleList.addAll(userUniversityScheduleRepository.findByUserIdAndDate(userId, date)
                .stream().map(UserUniversitySchedule::getUniversitySchedule)
                .map(GetDailyCalendarResponse::from)
                .filter(dto -> {
                    String key = dto.getStartDate() + "|" +
                            dto.getEndDate()   + "|" +
                            dto.getScheduleName() + "|" +
                            dto.getUniversityAdmissionStage();
                    return seen.add(key);
                })
                .toList());

        scheduleList.sort(
                Comparator.comparingLong((GetDailyCalendarResponse schedule) ->
                                DateTimeUtils.durationInSeconds(schedule.getStartDate(), schedule.getEndDate()))
                        .thenComparing(GetDailyCalendarResponse::getStartDate)
                        .reversed()
        );

        return scheduleList;
    }
}
