package com.togedy.togedy_server_v2.domain.badge.dto;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RepresentativeBadgeResponse {

    private Long badgeId;
    private String badgeName;
    private String badgeImageUrl;
    private LocalDate acquiredDate;

    public static RepresentativeBadgeResponse of(
            Long badgeId,
            String badgeName,
            String badgeImageUrl,
            LocalDate acquiredDate
    ) {
        return RepresentativeBadgeResponse.builder()
                .badgeId(badgeId)
                .badgeName(badgeName)
                .badgeImageUrl(badgeImageUrl)
                .acquiredDate(acquiredDate)
                .build();
    }
}
