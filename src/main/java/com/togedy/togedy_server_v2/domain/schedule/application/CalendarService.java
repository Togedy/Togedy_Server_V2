package com.togedy.togedy_server_v2.domain.schedule.application;

import com.togedy.togedy_server_v2.domain.schedule.dto.GetDdayScheduleResponse;
import com.togedy.togedy_server_v2.domain.schedule.dao.UserScheduleRepository;
import com.togedy.togedy_server_v2.domain.schedule.dto.DailyScheduleListDto;
import com.togedy.togedy_server_v2.domain.schedule.dto.GetDailyCalendarResponse;
import com.togedy.togedy_server_v2.domain.schedule.dto.GetMonthlyCalendarResponse;
import com.togedy.togedy_server_v2.domain.schedule.dto.MonthlyScheduleListDto;
import com.togedy.togedy_server_v2.domain.schedule.entity.ScheduleComparable;
import com.togedy.togedy_server_v2.domain.schedule.entity.UserSchedule;
import com.togedy.togedy_server_v2.domain.university.dao.UserUniversityMethodRepository;
import com.togedy.togedy_server_v2.domain.user.dao.UserRepository;
import com.togedy.togedy_server_v2.domain.user.entity.User;
import com.togedy.togedy_server_v2.domain.user.exception.UserNotFoundException;
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
    private final UserUniversityMethodRepository userUniversityMethodRepository;

    /**
     * 유저가 해당 월에 보유하고 있는 개인 일정 및 대학 일정을 기간이 긴 순서대로 정렬하여 반환한다.
     *
     * @param month     년도 및 월 정보(yyyy-MM)
     * @param userId    유저ID
     * @return          기간 순으로 정렬된 월별 개인 일정 및 대학 일정 DTO
     */
    @Transactional(readOnly = true)
    public GetMonthlyCalendarResponse findMonthlyCalendar(YearMonth month, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        LocalDate startOfMonth = month.atDay(1);
        LocalDate endOfMonth = month.atEndOfMonth();

        List<MonthlyScheduleListDto> monthlyUserSchedule = findMonthlyUserSchedule(userId, startOfMonth, endOfMonth);
        monthlyUserSchedule.addAll(findMonthlyUniversitySchedule(userId, startOfMonth, endOfMonth));
        monthlyUserSchedule.sort(scheduleComparator());

        return GetMonthlyCalendarResponse.from(monthlyUserSchedule);
    }

    /**
     * 유저가 해당 날짜에 보유하고 있는 개인 일정 및 대학 일정을 기간이 긴 순서대로 정렬하여 반환한다.
     *
     * @param date      년도, 월, 날짜 정보 (yyyy-MM-dd)
     * @param userId    유저ID
     * @return          기간 순으로 정렬된 일별 유저 및 대학 일정 DTO
     */
    @Transactional(readOnly = true)
    public GetDailyCalendarResponse findDailyCalendar(LocalDate date, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        List<DailyScheduleListDto> dailyScheduleList = new ArrayList<>(findDailyUserSchedule(userId, date));
        dailyScheduleList.addAll(findDailyUniversitySchedule(userId, date));
        dailyScheduleList.sort(scheduleComparator());

        return GetDailyCalendarResponse.from(dailyScheduleList);
    }

    /**
     * 유저가 D-Day 설정한 개인 일정을 조회한다.
     *
     * @param userId    유저ID
     * @return          D-Day 설정한 개인 일정이 존재 여부 및 일정 정보 반환
     */
    @Transactional(readOnly = true)
    public GetDdayScheduleResponse findDdaySchedule(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Optional<UserSchedule> dDaySchedule = userScheduleRepository.findByUserIdAndDDayTrue(userId);

        if (dDaySchedule.isPresent()) {
            return GetDdayScheduleResponse.of(dDaySchedule.get(),
                    DateTimeUtils.calculateRemainingDays(dDaySchedule.get().getStartDate()));
        }

        return GetDdayScheduleResponse.temp();
    }

    /**
     * 유저가 보유 중인 해당 월의 개인 일정을 조회한다.
     *
     * @param userId    유저ID
     * @param month     년도 및 월 정보 (yyyy-MM)
     * @return          월별 일정 DTO List
     */
    private List<MonthlyScheduleListDto> findMonthlyUserSchedule(
            Long userId,
            LocalDate startOfMonth,
            LocalDate endOfMonth
    ) {
        return userScheduleRepository
                .findByUserIdAndYearAndMonth(userId, startOfMonth, endOfMonth)
                .stream()
                .map(MonthlyScheduleListDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 유저가 보유 중인 해당 월의 대학 일정을 조회한다.
     *
     * @param userId    유저 ID
     * @param month     년도 및 월 정보 (yyyy-MM)
     * @return          월별 일정 DTO List
     */
    private List<MonthlyScheduleListDto> findMonthlyUniversitySchedule(
            Long userId,
            LocalDate startOfMonth,
            LocalDate endOfMonth
    ) {
        return userUniversityMethodRepository
                .findByUserIdAndYearAndMonth(userId, startOfMonth, endOfMonth)
                .stream()
                .flatMap(uus -> uus.getUniversityAdmissionMethod().getUniversityAdmissionScheduleList().stream())
                .collect(Collectors.toMap(
                        uas -> uas.getUniversitySchedule().getId(),
                        Function.identity(),
                        (first, second) -> first,
                        LinkedHashMap::new
                ))
                .values().stream()
                .map(MonthlyScheduleListDto::from)
                .toList();
    }

    /**
     * 유저가 보유 중인 해당 날짜의 개인 일정을 조회한다.
     *
     * @param userId    유저ID
     * @param date      년도, 월, 날짜 정보 (yyyy-MM-dd)
     * @return          일별 일정 DTO List
     */
    private List<DailyScheduleListDto> findDailyUserSchedule(Long userId, LocalDate date) {
        return userScheduleRepository
                .findByUserIdAndDate(userId, date)
                .stream()
                .map(DailyScheduleListDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 유저가 보유 중인 해당 날짜의 대학 일정을 조회한다.
     *
     * @param userId    유저ID
     * @param date      년도, 월, 날짜 정보 (yyyy-MM-dd)
     * @return          일별 일정 DTO List
     */
    private List<DailyScheduleListDto> findDailyUniversitySchedule(Long userId, LocalDate date) {
        return userUniversityMethodRepository
                .findByUserIdAndDate(userId, date)
                .stream()
                .flatMap(uum -> uum.getUniversityAdmissionMethod().getUniversityAdmissionScheduleList().stream())
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

    /**
     * 일정의 시작 및 종료 날짜 혹은 시간을 기준으로 정렬한다.
     *
     * @return  기간 및 시작 시간 순으로 정렬하는 Comparator
     */
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
