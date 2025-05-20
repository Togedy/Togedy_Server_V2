package com.togedy.togedy_server_v2.domain.schedule.application;

import com.togedy.togedy_server_v2.domain.schedule.dto.GetDdayScheduleResponse;
import com.togedy.togedy_server_v2.domain.schedule.dao.UserScheduleRepository;
import com.togedy.togedy_server_v2.domain.schedule.dto.DailyScheduleListDto;
import com.togedy.togedy_server_v2.domain.schedule.dto.GetDailyCalendarResponse;
import com.togedy.togedy_server_v2.domain.schedule.dto.GetMonthlyCalendarResponse;
import com.togedy.togedy_server_v2.domain.schedule.dto.MonthlyScheduleListDto;
import com.togedy.togedy_server_v2.domain.schedule.entity.ScheduleComparable;
import com.togedy.togedy_server_v2.domain.schedule.entity.UserSchedule;
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
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CalendarService {

    private final UserRepository userRepository;
    private final UserScheduleRepository userScheduleRepository;
    private final UserUniversityScheduleRepository userUniversityScheduleRepository;

    @Transactional(readOnly = true)
    public GetMonthlyCalendarResponse findMonthlyCalendar(YearMonth month, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(RuntimeException::new);

        List<MonthlyScheduleListDto> monthlyUserSchedule = findMonthlyUserSchedule(userId, month);
        monthlyUserSchedule.addAll(findMonthlyUniversitySchedule(userId, month));
        monthlyUserSchedule.sort(scheduleComparator());

        return GetMonthlyCalendarResponse.from(monthlyUserSchedule);
    }

    @Transactional(readOnly = true)
    public GetDailyCalendarResponse findDailyCalendar(LocalDate date, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(RuntimeException::new);

        List<DailyScheduleListDto> dailyScheduleList = new ArrayList<>(findDailyUserSchedule(userId, date));
        dailyScheduleList.addAll(findDailyUniversitySchedule(userId, date));
        dailyScheduleList.sort(scheduleComparator());

        return GetDailyCalendarResponse.from(dailyScheduleList);
    }

    @Transactional(readOnly = true)
    public GetDdayScheduleResponse findDdaySchedule(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(RuntimeException::new);

        Optional<UserSchedule> dDaySchedule = userScheduleRepository.findByUserIdAndDDayTrue(userId);

        if (dDaySchedule.isPresent()) {
            return GetDdayScheduleResponse.of(dDaySchedule.get(),
                    DateTimeUtils.calculateRemainingDays(dDaySchedule.get().getStartDate()));
        }

        return GetDdayScheduleResponse.temp();
    }

    private List<MonthlyScheduleListDto> findMonthlyUserSchedule(Long userId, YearMonth month) {
        return userScheduleRepository
                .findByUserIdAndYearAndMonth(userId, month.getYear(), month.getMonthValue())
                .stream()
                .map(MonthlyScheduleListDto::from)
                .collect(Collectors.toList());
    }

    private List<MonthlyScheduleListDto> findMonthlyUniversitySchedule(Long userId, YearMonth month) {
        return userUniversityScheduleRepository
                .findByUserIdAndYearAndMonth(userId, month.getYear(), month.getMonthValue())
                .stream()
                .flatMap(uus -> uus.getUniversitySchedule().getAdmissionScheduleList().stream())
                .collect(Collectors.toMap(
                        as -> as.getUniversitySchedule().getId(),
                        Function.identity(),
                        (first, second) -> first,
                        LinkedHashMap::new
                ))
                .values().stream()
                .map(MonthlyScheduleListDto::from)
                .toList();
    }

    private List<DailyScheduleListDto> findDailyUserSchedule(Long userId, LocalDate date) {
        return userScheduleRepository
                .findByUserIdAndDate(userId, date)
                .stream()
                .map(DailyScheduleListDto::from)
                .collect(Collectors.toList());
    }

    private List<DailyScheduleListDto> findDailyUniversitySchedule(Long userId, LocalDate date) {
        return userUniversityScheduleRepository
                .findByUserIdAndDate(userId, date)
                .stream()
                .flatMap(uus -> uus.getUniversitySchedule().getAdmissionScheduleList().stream())
                .collect(Collectors.toMap(
                        as -> as.getUniversitySchedule().getId(),
                        Function.identity(),
                        (first, second) -> first,
                        LinkedHashMap::new
                ))
                .values().stream()
                .map(DailyScheduleListDto::from)
                .toList();
    }

    private Comparator<ScheduleComparable> scheduleComparator() {
        return Comparator
                .<ScheduleComparable>comparingLong(sc ->
                        DateTimeUtils.durationInSeconds(
                                sc.getStartDate(), sc.getStartTime(),
                                sc.getEndDate(),   sc.getEndTime()))
                .thenComparing(sc ->
                        DateTimeUtils.toStartDateTime(sc.getStartDate(), sc.getStartTime()))
                .reversed();
    }
}
