package com.togedy.togedy_server_v2.domain.schedule.application;

import com.togedy.togedy_server_v2.domain.schedule.dao.UserScheduleRepository;
import com.togedy.togedy_server_v2.domain.schedule.dto.GetMonthlyCalendarsResponse;
import com.togedy.togedy_server_v2.domain.schedule.dto.ScheduleListDto;
import com.togedy.togedy_server_v2.domain.university.dao.UserUniversityScheduleRepository;
import com.togedy.togedy_server_v2.domain.university.entity.UserUniversitySchedule;
import com.togedy.togedy_server_v2.domain.user.dao.UserRepository;
import com.togedy.togedy_server_v2.domain.user.entity.User;
import com.togedy.togedy_server_v2.global.util.DateTimeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalendarService {

    private final UserRepository userRepository;
    private final UserScheduleRepository userScheduleRepository;
    private final UserUniversityScheduleRepository userUniversityScheduleRepository;


    public GetMonthlyCalendarsResponse findMonthlyCalendar(YearMonth yearMonth, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(RuntimeException::new);

        List<ScheduleListDto> scheduleList = new ArrayList<>(userScheduleRepository
                .findByUserIdAndYearAndMonth(userId, yearMonth.getYear(), yearMonth.getMonthValue())
                .stream().map(ScheduleListDto::from).toList());

        scheduleList.addAll(userUniversityScheduleRepository.findByUserAndYearAndMonth(userId, yearMonth.getYear(), yearMonth.getMonthValue())
                        .stream().map(UserUniversitySchedule::getUniversitySchedule)
                        .map(ScheduleListDto::from)
                        .toList());

        scheduleList.sort(
                Comparator.comparingLong((ScheduleListDto schedule) ->
                                DateTimeUtils.durationInSeconds(schedule.getStartDate(), schedule.getEndDate()))
                        .thenComparing(ScheduleListDto::getStartDate)
                        .reversed()
        );

        return GetMonthlyCalendarsResponse.from(scheduleList);
    }
}
