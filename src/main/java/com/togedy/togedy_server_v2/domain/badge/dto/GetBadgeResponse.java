package com.togedy.togedy_server_v2.domain.badge.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetBadgeResponse {

    private Long badgeId;
    private String badgeName;
    private String badgeImageUrl;
    private String badgeDescription;

    public static GetBadgeResponse of(
            Long badgeId,
            String badgeName,
            String badgeImageUrl,
            String badgeDescription
    ) {
        return GetBadgeResponse.builder()
                .badgeId(badgeId)
                .badgeName(badgeName)
                .badgeImageUrl(badgeImageUrl)
                .badgeDescription(badgeDescription)
                .build();
    }
}
