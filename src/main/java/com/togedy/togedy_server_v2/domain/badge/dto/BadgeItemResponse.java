package com.togedy.togedy_server_v2.domain.badge.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BadgeItemResponse {

    private Long badgeId;
    private String badgeName;
    private String badgeImageUrl;
    private int badgeCount;

    public static BadgeItemResponse of(
            Long badgeId,
            String badgeName,
            String badgeImageUrl,
            int badgeCount
    ) {
        return BadgeItemResponse.builder()
                .badgeId(badgeId)
                .badgeName(badgeName)
                .badgeImageUrl(badgeImageUrl)
                .badgeCount(badgeCount)
                .build();
    }
}
