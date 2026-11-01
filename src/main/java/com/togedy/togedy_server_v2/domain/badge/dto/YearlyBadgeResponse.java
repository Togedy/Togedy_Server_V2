package com.togedy.togedy_server_v2.domain.badge.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class YearlyBadgeResponse {

    private int year;
    private List<BadgeItemResponse> badges;

    public static YearlyBadgeResponse of(int year, List<BadgeItemResponse> badges) {
        return YearlyBadgeResponse.builder()
                .year(year)
                .badges(badges)
                .build();
    }
}
