package com.togedy.togedy_server_v2.domain.schedule.application;

import com.togedy.togedy_server_v2.domain.schedule.dao.UserScheduleRepository;
import com.togedy.togedy_server_v2.domain.schedule.dto.response.DailyScheduleInfo;
import com.togedy.togedy_server_v2.domain.schedule.dto.response.GetDailyCalendarResponse;
import com.togedy.togedy_server_v2.domain.schedule.dto.response.GetDdayScheduleResponse;
import com.togedy.togedy_server_v2.domain.schedule.dto.response.GetMonthlyCalendarResponse;
import com.togedy.togedy_server_v2.domain.schedule.dto.response.MonthlyScheduleInfo;
import com.togedy.togedy_server_v2.domain.schedule.entity.ScheduleComparable;
import com.togedy.togedy_server_v2.domain.schedule.entity.UserSchedule;
import com.togedy.togedy_server_v2.domain.university.dao.UserUniversityMethodRepository;
import com.togedy.togedy_server_v2.global.util.TimeUtil;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CalendarService {

    private final UserScheduleRepository userScheduleRepository;
    private final UserUniversityMethodRepository userUniversityMethodRepository;

    /**
     * 유저의 해당 월에 보유하고 있는 개인 일정 및 대학 일정을 기간이 긴 순서대로 정렬하여 반환한다.
     *
     * @param month  년도 및 월 정보(yyyy-MM)
     * @param userId 유저ID
     * @return 기간 순으로 정렬된 월별 개인 일정 및 대학 일정 DTO
     */
    public GetMonthlyCalendarResponse findMonthlyCalendar(YearMonth month, Long userId) {
        LocalDate startOfMonth = month.atDay(1);
        LocalDate endOfMonth = month.atEndOfMonth();

        List<MonthlyScheduleInfo> monthlySchedules = findMonthlyUserSchedule(userId, startOfMonth, endOfMonth);
        monthlySchedules.addAll(findMonthlyUniversitySchedule(userId, startOfMonth, endOfMonth));
        monthlySchedules.sort(scheduleComparator());

        return GetMonthlyCalendarResponse.from(monthlySchedules);
    }

    /**
     * 유저의 D-Day 일정까지 남은 일 수와 함께 해당 날짜에 보유하고 있는 개인 일정 및 대학 일정을 기간이 긴 순서대로 정렬하여 반환한다.
     *
     * @param date   년도, 월, 날짜 정보 (yyyy-MM-dd)
     * @param userId 유저ID
     * @return D-day 일정까지 남은 일 수 및 기간 순으로 정렬된 일별 유저 및 대학 일정 DTO
     */
    public GetDailyCalendarResponse findDailyCalendar(LocalDate date, Long userId) {
        List<DailyScheduleInfo> dailySchedules = findDailyUserSchedule(userId, date);
        dailySchedules.addAll(findDailyUniversitySchedule(userId, date));
        dailySchedules.sort(scheduleComparator());
        return GetDailyCalendarResponse.from(calculateRemainingDays(date, userId), dailySchedules);
    }

    /**
     * 유저가 D-Day 설정한 개인 일정을 조회한다.
     *
     * @param userId 유저ID
     * @return D-Day 설정한 개인 일정이 존재 여부 및 일정 정보 반환
     */
    public GetDdayScheduleResponse findDdaySchedule(Long userId) {
        Optional<UserSchedule> dDaySchedule = userScheduleRepository.findByUserIdAndDDayTrue(userId);

        if (dDaySchedule.isPresent()) {
            return GetDdayScheduleResponse.of(
                    dDaySchedule.get(),
                    TimeUtil.calculateDaysUntil(TimeUtil.todayInStudyZone(), dDaySchedule.get().getStartDate())
            );
        }

        return GetDdayScheduleResponse.temp();
    }

    /**
     * 유저가 보유 중인 해당 월의 개인 일정을 조회한다.
     *
     * @param userId       유저ID
     * @param startOfMonth 일정 시작 날짜
     * @param endOfMonth   일정 종료 날짜
     * @return 월별 일정 DTO List
     */
    private List<MonthlyScheduleInfo> findMonthlyUserSchedule(
            Long userId,
            LocalDate startOfMonth,
            LocalDate endOfMonth
    ) {
        return userScheduleRepository
                .findByUserIdAndYearAndMonth(userId, startOfMonth, endOfMonth)
                .stream()
                .map(MonthlyScheduleInfo::from)
                .collect(Collectors.toList());
    }

    /**
     * 유저가 보유 중인 해당 월의 대학 일정을 조회한다.
     *
     * @param userId       유저 ID
     * @param startOfMonth 일정 시작 날짜
     * @param endOfMonth   일정 종료 날짜
     * @return 월별 일정 DTO List
     */
    private List<MonthlyScheduleInfo> findMonthlyUniversitySchedule(
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
                .map(MonthlyScheduleInfo::from)
                .toList();
    }

    /**
     * 유저가 보유 중인 해당 날짜의 개인 일정을 조회한다.
     *
     * @param userId 유저ID
     * @param date   년도, 월, 날짜 정보 (yyyy-MM-dd)
     * @return 일별 일정 DTO List
     */
    private List<DailyScheduleInfo> findDailyUserSchedule(Long userId, LocalDate date) {
        return userScheduleRepository
                .findByUserIdAndDate(userId, date)
                .stream()
                .map(DailyScheduleInfo::from)
                .collect(Collectors.toList());
    }

    /**
     * 유저가 보유 중인 해당 날짜의 대학 일정을 조회한다.
     *
     * @param userId 유저ID
     * @param date   년도, 월, 날짜 정보 (yyyy-MM-dd)
     * @return 일별 일정 DTO List
     */
    private List<DailyScheduleInfo> findDailyUniversitySchedule(Long userId, LocalDate date) {
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
                .map(DailyScheduleInfo::from)
                .toList();
    }

    /**
     * 일정의 시작 및 종료 날짜 혹은 시간을 기준으로 정렬한다.
     *
     * @return 기간 및 시작 시간 순으로 정렬하는 Comparator
     */
    private Comparator<ScheduleComparable> scheduleComparator() {
        return Comparator
                .<ScheduleComparable>comparingLong(sc ->
                        TimeUtil.calculateDurationInSeconds(
                                sc.getStartDate(), sc.getStartTime(),
                                sc.getEndDate(), sc.getEndTime()))
                .thenComparing(sc ->
                        TimeUtil.toStartDateTime(sc.getStartDate(), sc.getStartTime()))
                .reversed();
    }

    /**
     * 해당 유저의 D-Day 일정으로부터 남은 일 수를 반환한다.
     * <p>
     * D-Day 일정이 존재하지 않는 경우 {@code null}을 반환한다.
     * </p>
     *
     * @param date   년도, 월, 날짜 정보 (yyyy-MM-dd)
     * @param userId 유저 ID
     * @return D-Day 일정으로부터 남은 일 수, 일정이 존재하지 않는 경우 {@code null}
     */
    private Integer calculateRemainingDays(LocalDate date, Long userId) {
        Optional<UserSchedule> dDaySchedule = userScheduleRepository.findByUserIdAndDDayTrue(userId);

        if (dDaySchedule.isPresent()) {
            return TimeUtil.calculateDaysUntil(date, dDaySchedule.get().getStartDate());
        }

        return null;
    }
}
