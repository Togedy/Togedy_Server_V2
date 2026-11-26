package com.togedy.togedy_server_v2.domain.badge.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetMyBadgesResponse {

    private List<YearlyBadgeResponse> yearlyBadges;

    public static GetMyBadgesResponse of(List<YearlyBadgeResponse> yearlyBadges) {
        return GetMyBadgesResponse.builder()
                .yearlyBadges(yearlyBadges)
                .build();
    }
}
