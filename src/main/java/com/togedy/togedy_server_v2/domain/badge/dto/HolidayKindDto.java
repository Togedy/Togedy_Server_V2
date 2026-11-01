package com.togedy.togedy_server_v2.domain.badge.dto;

import com.togedy.togedy_server_v2.domain.badge.entity.HolidayKind;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HolidayKindDto {

    private Long holidayKindId;

    private String name;

    private String badgeImageUrl;

    private boolean badgeEnabled;

    public static HolidayKindDto of(HolidayKind holidayKind) {
        return HolidayKindDto.builder()
                .holidayKindId(holidayKind.getId())
                .name(holidayKind.getName())
                .badgeImageUrl(holidayKind.getBadgeImageUrl())
                .badgeEnabled(holidayKind.isBadgeEnabled())
                .build();
    }
}
