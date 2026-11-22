package com.togedy.togedy_server_v2.domain.badge.application;

import com.togedy.togedy_server_v2.domain.badge.dao.HolidayKindRepository;
import com.togedy.togedy_server_v2.domain.badge.dao.HolidayRepository;
import com.togedy.togedy_server_v2.domain.badge.dto.HolidayDto;
import com.togedy.togedy_server_v2.domain.badge.dto.HolidayRequest;
import com.togedy.togedy_server_v2.domain.badge.entity.Holiday;
import com.togedy.togedy_server_v2.domain.badge.entity.HolidayKind;
import com.togedy.togedy_server_v2.domain.badge.enums.DateRuleType;
import com.togedy.togedy_server_v2.domain.badge.exception.DuplicateHolidayPeriodException;
import com.togedy.togedy_server_v2.domain.badge.exception.HolidayAlreadyEndedException;
import com.togedy.togedy_server_v2.domain.badge.exception.HolidayKindNotFoundException;
import com.togedy.togedy_server_v2.domain.badge.exception.HolidayNotFoundException;
import com.togedy.togedy_server_v2.domain.badge.exception.InvalidHolidayPeriodException;
import com.togedy.togedy_server_v2.domain.badge.exception.RuleHolidayNotModifiableException;
import com.togedy.togedy_server_v2.global.util.LunarCalendarUtil;
import com.togedy.togedy_server_v2.global.util.TimeUtil;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HolidayService {

    private final HolidayRepository holidayRepository;
    private final HolidayKindRepository holidayKindRepository;

    /**
     * 연휴 목록을 시작일 기준 내림차순으로 조회한다.
     *
     * @return 연휴 목록
     */
    @Transactional(readOnly = true)
    public List<HolidayDto> findHolidays() {
        LocalDate studyDate = TimeUtil.currentStudyDate();
        return holidayRepository.findAll(Sort.by(Direction.DESC, "startDate"))
                .stream()
                .map(holiday -> HolidayDto.of(holiday, studyDate))
                .toList();
    }

    /**
     * 단일 연휴를 조회한다.
     *
     * @param holidayId 조회 대상 연휴 ID
     * @return 연휴
     * @throws HolidayNotFoundException 해당 연휴가 존재하지 않는 경우
     */
    @Transactional(readOnly = true)
    public HolidayDto findHoliday(Long holidayId) {
        return HolidayDto.of(getHoliday(holidayId), TimeUtil.currentStudyDate());
    }

    /**
     * 연휴를 등록한다.
     * <p>
     * 어드민은 날짜 규칙이 없는(MANUAL) 종류의 연휴만 단일 날짜로 등록한다.
     * </p>
     *
     * @param request 연휴 요청 DTO
     * @throws HolidayKindNotFoundException      해당 연휴 종류가 존재하지 않는 경우
     * @throws RuleHolidayNotModifiableException 날짜 규칙이 있는 종류인 경우
     * @throws InvalidHolidayPeriodException     이미 지난 날짜인 경우
     * @throws DuplicateHolidayPeriodException   같은 날짜에 같은 종류의 연휴가 존재하는 경우
     */
    @Transactional
    public void generateHoliday(HolidayRequest request) {
        HolidayKind holidayKind = getManualHolidayKind(request.getHolidayKindId());
        validateDate(request.getDate());
        if (holidayRepository.existsByHolidayKindIdAndStartDate(holidayKind.getId(), request.getDate())) {
            throw new DuplicateHolidayPeriodException();
        }

        Holiday holiday = Holiday.builder()
                .holidayKind(holidayKind)
                .startDate(request.getDate())
                .endDate(request.getDate())
                .build();

        holidayRepository.save(holiday);
    }

    /**
     * 연휴를 수정한다.
     * <p>
     * 판정이 끝난 연휴와 날짜 규칙으로 생성된 연휴는 수정할 수 없다.
     * </p>
     *
     * @param request   연휴 요청 DTO
     * @param holidayId 수정 대상 연휴 ID
     * @throws HolidayNotFoundException          해당 연휴가 존재하지 않는 경우
     * @throws HolidayAlreadyEndedException      이미 종료되어 판정이 끝난 연휴인 경우
     * @throws RuleHolidayNotModifiableException 날짜 규칙으로 생성된 연휴이거나, 날짜 규칙이 있는 종류로 바꾸는 경우
     * @throws HolidayKindNotFoundException      해당 연휴 종류가 존재하지 않는 경우
     * @throws InvalidHolidayPeriodException     이미 지난 날짜인 경우
     * @throws DuplicateHolidayPeriodException   같은 날짜에 같은 종류의 연휴가 존재하는 경우
     */
    @Transactional
    public void modifyHoliday(HolidayRequest request, Long holidayId) {
        Holiday holiday = getModifiableHoliday(holidayId);
        HolidayKind holidayKind = getManualHolidayKind(request.getHolidayKindId());
        validateDate(request.getDate());
        if (holidayRepository.existsByHolidayKindIdAndStartDateAndIdNot(
                holidayKind.getId(), request.getDate(), holidayId)) {
            throw new DuplicateHolidayPeriodException();
        }

        holiday.update(holidayKind, request.getDate(), request.getDate());
    }

    /**
     * 연휴를 삭제한다.
     *
     * @param holidayId 삭제 대상 연휴 ID
     * @throws HolidayNotFoundException          해당 연휴가 존재하지 않는 경우
     * @throws HolidayAlreadyEndedException      이미 종료되어 판정이 끝난 연휴인 경우
     * @throws RuleHolidayNotModifiableException 날짜 규칙으로 생성된 연휴인 경우
     */
    @Transactional
    public void removeHoliday(Long holidayId) {
        holidayRepository.delete(getModifiableHoliday(holidayId));
    }

    /**
     * 날짜 규칙이 있는 연휴 종류로 해당 연도의 연휴를 생성한다.
     * <p>
     * 설날·추석처럼 여러 날인 연휴는 맞닿은 주말까지 연휴 기간에 포함한다. 같은 종류, 같은 시작일의 연휴가 이미 있으면 건너뛰므로 여러 번 실행해도 결과가 같다.
     * 판정이 끝난 지난 연휴는 생성하지 않는다.
     * </p>
     *
     * @param year 생성 대상 연도
     */
    @Transactional
    public void generateRuleHolidays(int year) {
        LocalDate studyDate = TimeUtil.currentStudyDate();

        for (HolidayKind holidayKind : holidayKindRepository.findAllByDateRuleTypeNot(DateRuleType.MANUAL)) {
            LocalDate ruleDate = resolveRuleDate(holidayKind, year);
            LocalDate startDate = ruleDate.minusDays(holidayKind.getDaysBefore());
            LocalDate endDate = ruleDate.plusDays(holidayKind.getDaysAfter());
            if (holidayKind.isLongHoliday()) {
                startDate = extendToAdjacentWeekend(startDate, -1);
                endDate = extendToAdjacentWeekend(endDate, 1);
            }

            Holiday holiday = Holiday.builder()
                    .holidayKind(holidayKind)
                    .startDate(startDate)
                    .endDate(endDate)
                    .build();
            if (holiday.isEnded(studyDate)
                    || holidayRepository.existsByHolidayKindIdAndStartDate(holidayKind.getId(), startDate)) {
                continue;
            }
            holidayRepository.save(holiday);
        }
    }

    /**
     * 연휴 종류의 날짜 규칙으로 해당 연도의 규칙 날짜(양력)를 구한다.
     *
     * @param holidayKind 날짜 규칙이 있는 연휴 종류
     * @param year        대상 연도
     * @return 규칙 날짜 (예: 추석은 음력 8월 15일의 양력 날짜)
     */
    private LocalDate resolveRuleDate(HolidayKind holidayKind, int year) {
        if (holidayKind.getDateRuleType() == DateRuleType.LUNAR) {
            return LunarCalendarUtil.toSolarDate(year, holidayKind.getRuleMonth(), holidayKind.getRuleDay());
        }
        return LocalDate.of(year, holidayKind.getRuleMonth(), holidayKind.getRuleDay());
    }

    /**
     * 날짜에 맞닿은 주말이 있으면 주말 끝까지 기간을 넓힌다.
     *
     * @param date      연휴의 시작일 또는 종료일
     * @param direction 앞쪽으로 넓히면 -1, 뒤쪽으로 넓히면 1
     * @return 맞닿은 주말까지 넓힌 날짜
     */
    private LocalDate extendToAdjacentWeekend(LocalDate date, int direction) {
        LocalDate extended = date;
        while (isWeekend(extended.plusDays(direction))) {
            extended = extended.plusDays(direction);
        }
        return extended;
    }

    private boolean isWeekend(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }

    /**
     * 연휴 날짜가 등록 가능한지 검증한다.
     *
     * @param date 연휴 날짜
     * @throws InvalidHolidayPeriodException 이미 지난 날짜인 경우
     */
    private void validateDate(LocalDate date) {
        if (date.isBefore(TimeUtil.currentStudyDate())) {
            throw new InvalidHolidayPeriodException();
        }
    }

    /**
     * 수정·삭제할 수 있는 연휴를 조회한다.
     *
     * @param holidayId 조회 대상 연휴 ID
     * @return 판정 전인 수동 연휴
     * @throws HolidayNotFoundException          해당 연휴가 존재하지 않는 경우
     * @throws RuleHolidayNotModifiableException 날짜 규칙으로 생성된 연휴인 경우
     * @throws HolidayAlreadyEndedException      이미 종료되어 판정이 끝난 연휴인 경우
     */
    private Holiday getModifiableHoliday(Long holidayId) {
        Holiday holiday = getHoliday(holidayId);
        if (!holiday.getHolidayKind().isManual()) {
            throw new RuleHolidayNotModifiableException();
        }
        if (holiday.isEnded(TimeUtil.currentStudyDate())) {
            throw new HolidayAlreadyEndedException();
        }
        return holiday;
    }

    /**
     * 어드민이 직접 날짜를 지정할 수 있는 연휴 종류를 조회한다.
     *
     * @param holidayKindId 조회 대상 연휴 종류 ID
     * @return 날짜 규칙이 없는(MANUAL) 연휴 종류
     * @throws HolidayKindNotFoundException      해당 연휴 종류가 존재하지 않는 경우
     * @throws RuleHolidayNotModifiableException 날짜 규칙이 있는 종류인 경우
     */
    private HolidayKind getManualHolidayKind(Long holidayKindId) {
        HolidayKind holidayKind = getHolidayKind(holidayKindId);
        if (!holidayKind.isManual()) {
            throw new RuleHolidayNotModifiableException();
        }
        return holidayKind;
    }

    /**
     * 연휴를 조회한다.
     *
     * @param holidayId 조회 대상 연휴 ID
     * @return 연휴
     * @throws HolidayNotFoundException 해당 연휴가 존재하지 않는 경우
     */
    private Holiday getHoliday(Long holidayId) {
        return holidayRepository.findById(holidayId)
                .orElseThrow(HolidayNotFoundException::new);
    }

    /**
     * 연휴 종류를 조회한다.
     *
     * @param holidayKindId 조회 대상 연휴 종류 ID
     * @return 연휴 종류
     * @throws HolidayKindNotFoundException 해당 연휴 종류가 존재하지 않는 경우
     */
    private HolidayKind getHolidayKind(Long holidayKindId) {
        return holidayKindRepository.findById(holidayKindId)
                .orElseThrow(HolidayKindNotFoundException::new);
    }
}
