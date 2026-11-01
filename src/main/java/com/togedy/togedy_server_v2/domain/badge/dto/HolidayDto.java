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

    public static HolidayDto of(Holiday holiday, LocalDate studyDate) {
        return HolidayDto.builder()
                .holidayId(holiday.getId())
                .holidayKindId(holiday.getHolidayKind().getId())
                .name(holiday.getHolidayKind().getName())
                .startDate(holiday.getStartDate())
                .endDate(holiday.getEndDate())
                .ended(holiday.isEnded(studyDate))
                .build();
    }
}
