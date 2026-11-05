package com.togedy.togedy_server_v2.domain.badge.application;

import com.togedy.togedy_server_v2.domain.badge.dao.HolidayKindRepository;
import com.togedy.togedy_server_v2.domain.badge.dao.HolidayRepository;
import com.togedy.togedy_server_v2.domain.badge.dto.HolidayDto;
import com.togedy.togedy_server_v2.domain.badge.dto.HolidayRequest;
import com.togedy.togedy_server_v2.domain.badge.entity.Holiday;
import com.togedy.togedy_server_v2.domain.badge.entity.HolidayKind;
import com.togedy.togedy_server_v2.domain.badge.exception.DuplicateHolidayPeriodException;
import com.togedy.togedy_server_v2.domain.badge.exception.HolidayAlreadyEndedException;
import com.togedy.togedy_server_v2.domain.badge.exception.HolidayKindNotFoundException;
import com.togedy.togedy_server_v2.domain.badge.exception.HolidayNotFoundException;
import com.togedy.togedy_server_v2.domain.badge.exception.InvalidHolidayPeriodException;
import com.togedy.togedy_server_v2.global.util.TimeUtil;
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
     *
     * @param request 연휴 요청 DTO
     * @throws HolidayKindNotFoundException    해당 연휴 종류가 존재하지 않는 경우
     * @throws InvalidHolidayPeriodException   종료일이 시작일보다 앞서거나 이미 지난 경우
     * @throws DuplicateHolidayPeriodException 기간이 겹치는 연휴가 존재하는 경우
     */
    @Transactional
    public void generateHoliday(HolidayRequest request) {
        HolidayKind holidayKind = getHolidayKind(request.getHolidayKindId());
        validatePeriod(request, null);

        Holiday holiday = Holiday.builder()
                .holidayKind(holidayKind)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();

        holidayRepository.save(holiday);
    }

    /**
     * 연휴를 수정한다.
     * <p>
     * 연휴 뱃지의 periodKey가 연휴 시작일이므로, 판정이 끝난 연휴는 수정할 수 없다.
     * </p>
     *
     * @param request   연휴 요청 DTO
     * @param holidayId 수정 대상 연휴 ID
     * @throws HolidayNotFoundException        해당 연휴가 존재하지 않는 경우
     * @throws HolidayAlreadyEndedException    이미 종료되어 판정이 끝난 연휴인 경우
     * @throws HolidayKindNotFoundException    해당 연휴 종류가 존재하지 않는 경우
     * @throws InvalidHolidayPeriodException   종료일이 시작일보다 앞서거나 이미 지난 경우
     * @throws DuplicateHolidayPeriodException 기간이 겹치는 연휴가 존재하는 경우
     */
    @Transactional
    public void modifyHoliday(HolidayRequest request, Long holidayId) {
        Holiday holiday = getModifiableHoliday(holidayId);
        HolidayKind holidayKind = getHolidayKind(request.getHolidayKindId());
        validatePeriod(request, holidayId);

        holiday.update(holidayKind, request.getStartDate(), request.getEndDate());
    }

    /**
     * 연휴를 삭제한다.
     *
     * @param holidayId 삭제 대상 연휴 ID
     * @throws HolidayNotFoundException     해당 연휴가 존재하지 않는 경우
     * @throws HolidayAlreadyEndedException 이미 종료되어 판정이 끝난 연휴인 경우
     */
    @Transactional
    public void removeHoliday(Long holidayId) {
        holidayRepository.delete(getModifiableHoliday(holidayId));
    }

    /**
     * 연휴 기간이 등록 가능한지 검증한다.
     *
     * @param request           연휴 요청 DTO
     * @param excludedHolidayId 겹침 검사에서 제외할 연휴 ID (수정 시 자기 자신), 등록 시 {@code null}
     * @throws InvalidHolidayPeriodException   종료일이 시작일보다 앞서거나 이미 지난 경우
     * @throws DuplicateHolidayPeriodException 기간이 겹치는 연휴가 존재하는 경우
     */
    private void validatePeriod(HolidayRequest request, Long excludedHolidayId) {
        if (request.getEndDate().isBefore(request.getStartDate())
                || request.getEndDate().isBefore(TimeUtil.currentStudyDate())) {
            throw new InvalidHolidayPeriodException();
        }

        if (holidayRepository.existsOverlapping(request.getStartDate(), request.getEndDate(), excludedHolidayId)) {
            throw new DuplicateHolidayPeriodException();
        }
    }

    /**
     * 수정·삭제할 수 있는 연휴를 조회한다.
     *
     * @param holidayId 조회 대상 연휴 ID
     * @return 판정 전인 연휴
     * @throws HolidayNotFoundException     해당 연휴가 존재하지 않는 경우
     * @throws HolidayAlreadyEndedException 이미 종료되어 판정이 끝난 연휴인 경우
     */
    private Holiday getModifiableHoliday(Long holidayId) {
        Holiday holiday = getHoliday(holidayId);
        if (holiday.isEnded(TimeUtil.currentStudyDate())) {
            throw new HolidayAlreadyEndedException();
        }
        return holiday;
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
