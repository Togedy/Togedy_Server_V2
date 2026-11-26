package com.togedy.togedy_server_v2.domain.badge.dto;

import com.togedy.togedy_server_v2.domain.badge.entity.Holiday;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HolidayDto {

    private Long holidayId;

    private Long holidayKindId;

    private String name;

    private LocalDate startDate;

    private LocalDate endDate;

    private boolean ended;

    // 날짜 규칙으로 생성된 연휴는 어드민이 수정·삭제할 수 없다.
    private boolean ruleGenerated;

    public static HolidayDto of(Holiday holiday, LocalDate studyDate) {
        return HolidayDto.builder()
                .holidayId(holiday.getId())
                .holidayKindId(holiday.getHolidayKind().getId())
                .name(holiday.getHolidayKind().getName())
                .startDate(holiday.getStartDate())
                .endDate(holiday.getEndDate())
                .ended(holiday.isEnded(studyDate))
                .ruleGenerated(!holiday.getHolidayKind().isManual())
                .build();
    }
}
